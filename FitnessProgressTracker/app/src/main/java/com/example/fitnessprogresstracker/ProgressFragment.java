package com.example.fitnessprogresstracker;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    private static TextView caloriesRemaining;
    private TextInputLayout foodInput, calorieInput;
    private ImageView deleteFood;
    private Button submitFood;
    private RecyclerView foodList;
    private List<String> lfoodNames = new ArrayList<>(), lcalCounts = new ArrayList<>();
    private List<ImageView> ldelButtons = new ArrayList<>();
    private String foodInputStr, calInputStr, calRemStr;
    private ProgressFoodListAdapter progAdapter;
    private boolean childExists;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressFragment newInstance(String param1, String param2) {
        ProgressFragment fragment = new ProgressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        caloriesRemaining = view.findViewById(R.id.tvCalRemaining);
        foodInput = view.findViewById(R.id.tilWeight);
        calorieInput = view.findViewById(R.id.tilCaloriesInput);
        foodList = view.findViewById(R.id.rvFoodList);
        submitFood = view.findViewById(R.id.btnAddFood);
        deleteFood = view.findViewById(R.id.ivFoodListDelete);

        changeUserCalories();
        checkForExistingLists();

        submitFood.setOnClickListener(v -> {
            foodInputStr = (foodInput.getEditText().getText()).toString();
            calInputStr = (calorieInput.getEditText().getText()).toString();

            if(validate()) {
                checkIfChildExists();
                addItemsToList();

                progAdapter = new ProgressFoodListAdapter(getActivity(), lfoodNames, lcalCounts, ldelButtons);

                foodList.setAdapter(progAdapter);
                foodList.setLayoutManager(new LinearLayoutManager(getActivity()));

                String remCals = Integer.toString(subtractRemainingCalories());
                caloriesRemaining.setText(remCals);

                firebaseAuth = FirebaseAuth.getInstance();
                databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
                databaseReference = databaseReference.child("userCalories");
                databaseReference.setValue(remCals);

                databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userFoodList");
                databaseReference.setValue(lfoodNames);
                databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userCalorieList");
                databaseReference.setValue(lcalCounts);

                foodInput.getEditText().setText("");
                calorieInput.getEditText().setText("");
            }
            delayButtonPress(submitFood);
        });

        return view;
    }

    private void changeUserCalories() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile userProfile = snapshot.getValue(UserProfile.class);
                caloriesRemaining.setText(userProfile.getUserCalories());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForExistingLists() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("userFoodList")) {
                    List<String> temp = new ArrayList<>();
                    List<ImageView> tempDelBtns = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userFoodList").getChildren()) {
                        String food = ds.getValue(String.class);
                        temp.add(food);
                        tempDelBtns.add(deleteFood);
                    }
                    lfoodNames = new ArrayList<>(temp);
                    ldelButtons = new ArrayList<>(tempDelBtns);

                }

                if(snapshot.hasChild("userCalorieList")) {
                    List<String> temp = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userCalorieList").getChildren()) {
                        String food = ds.getValue(String.class);
                        temp.add(food);
                    }
                    lcalCounts = new ArrayList<>(temp);
                }

                progAdapter = new ProgressFoodListAdapter(getActivity(), lfoodNames, lcalCounts, ldelButtons);
                foodList.setAdapter(progAdapter);
                foodList.setLayoutManager(new LinearLayoutManager(getActivity()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show(); } });
    }

    private void checkIfChildExists() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("userFoodList")) {
                    lfoodNames.clear();
                    lcalCounts.clear();
                    ldelButtons.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show(); } });
    }

    private void addItemsToList() {
        lfoodNames.add(foodInputStr);
        lcalCounts.add(calInputStr + " calories");
        ldelButtons.add(deleteFood);
    }

    private int subtractRemainingCalories() {
            calInputStr = (calorieInput.getEditText().getText()).toString();
            calRemStr = caloriesRemaining.getText().toString();
            int cal = Integer.parseInt(calInputStr);
            int calRem = Integer.parseInt(calRemStr);

        return calRem - cal;
    }

    public void revertRemainingCalories(int deletedCalories, int position) {

        firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("userFoodList")) {
                    calRemStr = caloriesRemaining.getText().toString();
                    int calRem = Integer.parseInt(calRemStr);

                    String revertedCals = Integer.toString(calRem + deletedCalories);
                    caloriesRemaining.setText(revertedCals);


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
                    databaseReference = databaseReference.child("userCalories");
                    databaseReference.setValue(revertedCals);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });



        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userFoodList");
        databaseReference.child(Integer.toString(position)).removeValue();
        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userCalorieList");
        databaseReference.child(Integer.toString(position)).removeValue();

        checkIfChildExists();
    }

    public void shrinkFirebaseList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("userFoodList")) {
                    List<String> temp = new ArrayList<>();
                    List<ImageView> tempDelBtns = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userFoodList").getChildren()) {
                        String food = ds.getValue(String.class);
                        temp.add(food);
                        tempDelBtns.add(deleteFood);
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
                    databaseReference.child("userFoodList").removeValue();
                    lfoodNames = new ArrayList<>(temp);
                    ldelButtons = new ArrayList<>(tempDelBtns);
                    databaseReference = databaseReference.child("userFoodList");
                    databaseReference.setValue(lfoodNames);
                }

                if(snapshot.hasChild("userCalorieList")) {
                    List<String> temp = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userCalorieList").getChildren()) {
                        String food = ds.getValue(String.class);
                        temp.add(food);
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
                    databaseReference.child("userCalorieList").removeValue();
                    lcalCounts = new ArrayList<>(temp);
                    databaseReference = databaseReference.child("userCalorieList");
                    databaseReference.setValue(lcalCounts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void delayButtonPress(Button myButton) {
        myButton.setEnabled(false);
        myButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                myButton.setEnabled(true);
            }
        }, 1000);
    }

    private Boolean validate() {
        Boolean result = false;

        foodInputStr = (foodInput.getEditText().getText()).toString();
        calInputStr = (calorieInput.getEditText().getText()).toString();

        if(foodInputStr.isEmpty() || calInputStr.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }

    public List<String> getFoodList() {
        return lfoodNames;
    }

    public List<String> getCalList() {
        return lcalCounts;
    }

    public List<ImageView> getDelBtnList() {
        return ldelButtons;
    }

}