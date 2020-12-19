package com.example.fitnessprogresstracker;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    private TextView caloriesRemaining;
    private TextInputLayout foodInput, calorieInput;
    private ImageView deleteFood;
    private Button submitFood;
    private RecyclerView foodList;
    private List<String> lfoodNames = new ArrayList<>(), lcalCounts = new ArrayList<>();
    private List<ImageView> ldelButtons = new ArrayList<>();
    private String foodInputStr, calInputStr, calRemStr;
    private ProgressFoodListAdapter progAdapter;

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
        foodInput = view.findViewById(R.id.tilFoodInput);
        calorieInput = view.findViewById(R.id.tilCaloriesInput);
        foodList = view.findViewById(R.id.rvFoodList);
        submitFood = view.findViewById(R.id.btnAddFood);
        deleteFood = view.findViewById(R.id.ivFoodListDelete);

        progAdapter = new ProgressFoodListAdapter(getActivity(), lfoodNames, lcalCounts, ldelButtons);

        submitFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodInputStr = (foodInput.getEditText().getText()).toString();
                calInputStr = (calorieInput.getEditText().getText()).toString();

                if(validate()) {
                    addItemsToList();
                    
                    progAdapter = new ProgressFoodListAdapter(getActivity(), lfoodNames, lcalCounts, ldelButtons);

                    foodList.setAdapter(progAdapter);
                    foodList.setLayoutManager(new LinearLayoutManager(getActivity()));

                    caloriesRemaining.setText(Integer.toString(subtractRemainingCalories()));
                    foodInput.getEditText().setText("");
                    calorieInput.getEditText().setText("");
                }
            }
        });




        return view;
    }

    private void setUpRecyclerView() {
        foodList.setAdapter(progAdapter);
        foodList.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(progAdapter));
        itemTouchHelper.attachToRecyclerView(foodList);
        progAdapter.notifyDataSetChanged();

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




}