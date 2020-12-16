package com.example.fitnessprogresstracker;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EditText calcAge, calcFeet, calcInches, calcLbs;
    private RadioGroup maleFemale;
    private Spinner activity;
    private Button calculate;
    String age, feet, inches, lbs, gender, activityStr;
    double activityLevel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        setUpUIViews(view);

        Spinner spinner = view.findViewById(R.id.spinnerActivity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.cactivity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        age = calcAge.getText().toString();
        feet = calcFeet.getText().toString();
        inches = calcInches.getText().toString();
        lbs = calcLbs.getText().toString();


        maleFemale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCalcMale:
                        gender = "m";
                        Toast.makeText(getContext(), gender, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rbCalcFemale:
                        gender = "f";
                        Toast.makeText(getContext(), gender, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // Inflate the layout for this fragment
        return view;
    }

    public void setUpUIViews(View view) {
        calcAge = (EditText) view.findViewById(R.id.etCalcAge);
        calcFeet = (EditText) view.findViewById(R.id.etCalcFeet);
        calcInches = (EditText) view.findViewById(R.id.etCalcInches);
        calcLbs = (EditText) view.findViewById(R.id.etCalcLbs);
        maleFemale = (RadioGroup) view.findViewById(R.id.rgMaleFemale);
        activity = (Spinner) view.findViewById(R.id.spinnerActivity);
        calculate = (Button) view.findViewById(R.id.btnCalculate);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        // Toast.makeText(parent.getContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
        activityStr = Integer.toString(position);
        setActivityLevel(activityStr);
    }

    public void setActivityLevel(String activityStr) {
        double[] activityLevels = {1, 1.2, 1.375, 1.465, 1.55, 1.75, 1.9};
        activityLevel = activityLevels[Integer.parseInt(activityStr)];
        Toast.makeText(getContext(), Double.toString(activityLevel), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}