package com.example.fitnessprogresstracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompareFragment extends Fragment {

    private ImageView addComparisons;
    private Button removeComparisons;
    public static List<String> uBeforeList = new ArrayList<>(), uAfterList = new ArrayList<>();
    private List<Button> deleteBtnList = new ArrayList<>();
    private static RecyclerView rvComparisons;
    public static CompareAdapter adapter;
    private static Activity activity;

    private static FirebaseAuth firebaseAuth;
    private static FirebaseStorage firebaseStorage;
    private static StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri fillerImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CompareFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompareFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompareFragment newInstance(String param1, String param2) {
        CompareFragment fragment = new CompareFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);
        setupUIviews(view);
        activity = getActivity();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());

        checkIfChildExists();
        addExistingComparisons();

        addComparisons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIfChildExists();
                fillURIList();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Lists: ", uBeforeList.toString());
                        adapter = new CompareAdapter(getActivity(), uBeforeList, uAfterList, deleteBtnList);
                        rvComparisons.setAdapter(adapter);
                        rvComparisons.setLayoutManager(new LinearLayoutManager(getActivity()));;
                    }
                }, 1000);

                delayButtonPress(addComparisons);
            }
        });

        return view;
    }

    private void fillURIList() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        storeImage("Before Pics", timeStamp);
        storeImage("After Pics", timeStamp);

        uBeforeList.add(timeStamp);
        uAfterList.add(timeStamp);
        deleteBtnList.add(removeComparisons);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userBeforeList");
        dbRef.setValue(uBeforeList);

        dbRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userAfterList");
        dbRef.setValue(uAfterList);
    }

    private void addExistingComparisons() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("userBeforeList")) {
                    List<String> temp = new ArrayList<>();
                    List<Button> tempDelBtns = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userBeforeList").getChildren()) {
                        String uniqueID = ds.getValue(String.class);
                        temp.add(uniqueID);
                        tempDelBtns.add(removeComparisons);
                    }
                    uBeforeList = new ArrayList<>(temp);
                    deleteBtnList = new ArrayList<>(tempDelBtns);
                }

                if(snapshot.hasChild("userAfterList")) {
                    List<String> temp = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userAfterList").getChildren()) {
                        String uniqueID = ds.getValue(String.class);
                        temp.add(uniqueID);
                    }
                    uAfterList = new ArrayList<>(temp);
                }

                adapter = new CompareAdapter(getActivity(), uBeforeList, uAfterList, deleteBtnList);
                rvComparisons.setAdapter(adapter);
                rvComparisons.setLayoutManager(new LinearLayoutManager(getActivity()));;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfChildExists() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChild("userAfterList")) {
                    uAfterList.clear();
                    uBeforeList.clear();
                    deleteBtnList.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show(); } });
    }

    public void shrinkFirebaseLists(int pos, String beforePic, String afterPic) {
        removeFromStorage(beforePic, afterPic);

        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userBeforeList");
        databaseReference.child(Integer.toString(pos)).removeValue();
        databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userAfterList");
        databaseReference.child(Integer.toString(pos)).removeValue();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("userBeforeList")) {
                    List<String> temp = new ArrayList<>();
                    List<Button> tempDelBtns = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userBeforeList").getChildren()) {
                        if(uBeforeList.size() > 0) {
                            String uniqueID = ds.getValue(String.class);
                            temp.add(uniqueID);
                            tempDelBtns.add(removeComparisons);
                        }
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
                    databaseReference.child("userBeforeList").removeValue();
                    uBeforeList = new ArrayList<>(temp);
                    deleteBtnList = new ArrayList<>(tempDelBtns);
                    databaseReference = databaseReference.child("userBeforeList");
                    databaseReference.setValue(uBeforeList);
                }

                if(snapshot.hasChild("userAfterList")) {
                    List<String> temp = new ArrayList<>();
                    for(DataSnapshot ds : snapshot.child("userAfterList").getChildren()) {
                        if(uBeforeList.size() > 0) {
                            String uniqueID = ds.getValue(String.class);
                            temp.add(uniqueID);
                        }
                    }
                    databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
                    databaseReference.child("userAfterList").removeValue();
                    uAfterList = new ArrayList<>(temp);
                    databaseReference = databaseReference.child("userAfterList");
                    databaseReference.setValue(uAfterList);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromStorage(String beforeUniqueID, String afterUniqueID) {
        StorageReference storage = firebaseStorage.getReference(firebaseAuth.getUid()).child("Images").child("Before Pics").child(beforeUniqueID);
        storage.delete();

        storage = firebaseStorage.getReference(firebaseAuth.getUid()).child("Images").child("After Pics").child(afterUniqueID);
        storage.delete();
    }

    public void storeImage(String child, String uniqueID) {
        fillerImage = Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.default_profile_picture);
        StorageReference imageReference = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("Images").child(child).child(uniqueID);
        UploadTask uploadTask = imageReference.putFile(fillerImage);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Fail: ", e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void
            onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Success: ", "Picture Successfully Uploaded.");
            }
        });
    }

    public void chooseImageFromGallery(int requestCode) {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(gallery, "Select Image"), requestCode);
    }

    private void setupUIviews(View view) {
        addComparisons = view.findViewById(R.id.ivAddComparison);
        rvComparisons = view.findViewById(R.id.rvComparisons);
        removeComparisons = view.findViewById(R.id.btnRemoveCompare);
    }

    public CompareAdapter getAdapter() {
        return adapter;
    }

    public List<String> getBeforeList() {
        return uBeforeList;
    }

    public List<String> getAfterList() { return uAfterList; }


    private void delayButtonPress(ImageView myButton) {
        myButton.setEnabled(false);
        myButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                myButton.setEnabled(true);
            }
        }, 2500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


}