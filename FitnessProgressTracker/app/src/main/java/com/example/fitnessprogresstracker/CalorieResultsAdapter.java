package com.example.fitnessprogresstracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CalorieResultsAdapter extends RecyclerView.Adapter<CalorieResultsAdapter.MyViewHolder> {

    List<String> lcaloriesNeeded, lpoundsLostPerWeek;
    Context context;
    RecyclerView.ViewHolder viewHolder;

    public CalorieResultsAdapter(Context ct, List<String> s1, List<String> s2) {
        context = ct;
        lcaloriesNeeded = s1;
        lpoundsLostPerWeek = s2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.calorie_results_row, parent, false);

        viewHolder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.caloriesNeeded.setText(lcaloriesNeeded.get(position));
        holder.poundsPerWeek.setText(lpoundsLostPerWeek.get(position));
    }

    @Override
    public int getItemCount() {
        return lcaloriesNeeded.size();
    }

    public Context getContext() {
        return context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView caloriesNeeded, poundsPerWeek;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            caloriesNeeded = itemView.findViewById(R.id.tvCaloriesNeeded);
            poundsPerWeek = itemView.findViewById(R.id.tvPoundsPerWeek);
        }
    }
}

