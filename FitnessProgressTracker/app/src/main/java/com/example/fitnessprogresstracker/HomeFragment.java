package com.example.fitnessprogresstracker;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextInputLayout tcalcAge, tcalcFeet, tcalcInches, tcalcLbs;
    private RadioGroup maleFemale;
    private Spinner activity;
    private Button calculate;
    private String age = "0", feet, inches, lbs, gender = "m", activityStr;
    private double calcActivityLevel;
    private TextView calcMaintainCal, calcMildCal, calcLosePoundCal, calcExtremeCal;

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

        maleFemale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCalcMale:
                        gender = "m";
                        break;
                    case R.id.rbCalcFemale:
                        gender = "f";
                        break;
                }
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age = (tcalcAge.getEditText().getText()).toString();
                feet = (tcalcFeet.getEditText().getText()).toString();
                inches = (tcalcInches.getEditText().getText()).toString();
                lbs = (tcalcLbs.getEditText().getText()).toString();
                if(validate()) {
                    calculateBMR(age, feet, inches, lbs);
                    delayButtonPress(calculate);
                }

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void setUpUIViews(View view) {
        tcalcAge = (TextInputLayout) view.findViewById(R.id.tilAge);
        tcalcFeet = (TextInputLayout) view.findViewById(R.id.tilFeet);
        tcalcInches= (TextInputLayout) view.findViewById(R.id.tilInches);
        tcalcLbs = (TextInputLayout) view.findViewById(R.id.tilWeight);
        maleFemale = (RadioGroup) view.findViewById(R.id.rgMaleFemale);
        activity = (Spinner) view.findViewById(R.id.spinnerActivity);
        calculate = (Button) view.findViewById(R.id.btnCalculate);
        calcMaintainCal = (TextView) view.findViewById(R.id.tvMaintainCalories);
        calcMildCal = (TextView) view.findViewById(R.id.tvMildCalories);
        calcLosePoundCal = (TextView) view.findViewById(R.id.tvLosePoundCalories);
        calcExtremeCal = (TextView) view.findViewById(R.id.tvExtremeCalories);
    }

    public void setActivityLevel(String activityStr) {
        double[] activityLevels = {1, 1.2, 1.375, 1.465, 1.55, 1.75, 1.9};
        calcActivityLevel = activityLevels[Integer.parseInt(activityStr)];
    }

    public double calculateBMR(String age, String feet, String inches, String lbs) {
        double bmr = 0;
        int bmrFinal = 0;
        int ageInt = Integer.parseInt(age);

        if(gender.equals("m")) {
            bmr = Math.ceil((10*lbs_to_kg(lbs)) + (6.25*(ft_to_cm(feet) + in_to_cm(inches))) - (5 * ageInt) + 5) * calcActivityLevel;
        } else {
            bmr = Math.ceil((10*lbs_to_kg(lbs)) + (6.25*(ft_to_cm(feet) + in_to_cm(inches))) - (5 * ageInt) - 161) * calcActivityLevel;
        }
        bmrFinal = (int) bmr;
        appendCalories(bmrFinal);

        return bmrFinal;
    }

    public void appendCalories(int bmr) {
        calcMaintainCal.setText("Maintain Weight: " + bmr + " calories/day");
        calcMildCal.setText("Mild Weight Loss: " + (bmr - 300) + " calories/day");
        calcLosePoundCal.setText("Weight Loss: " + (bmr - 500) + " calories/day");
        calcExtremeCal.setText("Extreme Weight Loss: " + (int)(bmr - 1000) + " calories/day");
    }

    public double ft_to_cm(String feet) {
        int ft = Integer.parseInt(feet);
        return ft * 30.48;
    }

    public double in_to_cm(String inch) {
        int in = Integer.parseInt(inch);
        return in * 2.54;
    }

    public double lbs_to_kg(String lbs) {
        int lb = Integer.parseInt(lbs);
        return lb * 0.453592;
    }

    private Boolean validate() {
        Boolean result = false;

        age = (tcalcAge.getEditText().getText()).toString();
        feet = (tcalcFeet.getEditText().getText()).toString();
        inches = (tcalcInches.getEditText().getText()).toString();
        lbs = (tcalcLbs.getEditText().getText()).toString();


        if(age.isEmpty() || feet.isEmpty() || inches.isEmpty() || lbs.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }

    private void delayButtonPress(Button myButton) {
        myButton.setEnabled(false);
        myButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                myButton.setEnabled(true);
            }
        }, 2500);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        activityStr = Integer.toString(position);
        setActivityLevel(activityStr);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}