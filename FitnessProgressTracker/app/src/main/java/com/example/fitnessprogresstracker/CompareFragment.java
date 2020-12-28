package com.example.fitnessprogresstracker;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
    private List<ImageView> beforeList = new ArrayList<>(), afterList = new ArrayList<>();
    private List<Button> deleteBtnList = new ArrayList<>();
    private RecyclerView rvComparisons;
    private CompareAdapter adapter;
    private ComparisonClickListener clickListener;

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

        addComparisons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImagesToList();

                adapter = new CompareAdapter(getActivity(), beforeList, afterList, deleteBtnList);
                rvComparisons.setAdapter(adapter);
                rvComparisons.setLayoutManager(new LinearLayoutManager(getActivity()));

                delayButtonPress(addComparisons);
            }
        });


        return view;
    }

    private void setupUIviews(View view) {
        addComparisons = view.findViewById(R.id.ivAddComparison);
        rvComparisons = view.findViewById(R.id.rvComparisons);
        removeComparisons = view.findViewById(R.id.btnRemoveCompare);
    }

    private void addImagesToList() {
        ImageView fillerImg = new ImageView(getActivity());
        fillerImg.setImageResource(R.drawable.ic_baseline_add_circle_24);
        beforeList.add(fillerImg);
        afterList.add(fillerImg);

        deleteBtnList.add(removeComparisons);
    }

    private void delayButtonPress(ImageView myButton) {
        myButton.setEnabled(false);
        myButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                myButton.setEnabled(true);
            }
        }, 2500);
    }



}