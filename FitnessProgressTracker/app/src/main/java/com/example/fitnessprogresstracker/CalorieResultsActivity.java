package com.example.fitnessprogresstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CalorieResultsActivity extends AppCompatActivity {

    List<String> calorieResults = new ArrayList<>(), poundsLostPerWeek = new ArrayList<>();
    RecyclerView calorieResultsList;
    CalorieResultsAdapter adapter;
    HomeFragment hf = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_results);
        addInfoToList();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calorieResultsList = (RecyclerView) findViewById(R.id.rvCalorieResultsList);
        calorieResults = hf.getCalorieResults();

        adapter = new CalorieResultsAdapter(this, calorieResults, poundsLostPerWeek);

        calorieResultsList.setAdapter(adapter);
        calorieResultsList.setLayoutManager(new LinearLayoutManager(CalorieResultsActivity.this));

    }

    private void addInfoToList() {
        poundsLostPerWeek.add("Maintain Weight");
        poundsLostPerWeek.add("Lose .5 lbs/week");
        poundsLostPerWeek.add("Lose 1 lbs/week");
        poundsLostPerWeek.add("Lose 2 lbs/week");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                hf.getCalorieResults().clear(); // Clear List in case user recalculates
        }
        return super.onOptionsItemSelected(item);
    }
}