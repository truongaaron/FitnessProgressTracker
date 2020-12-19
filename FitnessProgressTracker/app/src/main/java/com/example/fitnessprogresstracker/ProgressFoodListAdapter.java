package com.example.fitnessprogresstracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProgressFoodListAdapter extends RecyclerView.Adapter<ProgressFoodListAdapter.MyViewHolder> {

    ProgressFragment pf = new ProgressFragment();

    List<String> lfoodNames, lcalories;
    List<ImageView> deleteButtons;
    Context context;
    RecyclerView.ViewHolder viewHolder;

    public ProgressFoodListAdapter(Context ct, List<String> s1, List<String> s2, List<ImageView> images) {
        context = ct;
        lfoodNames = s1;
        lcalories = s2;
        deleteButtons = images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_food_row, parent, false);
        viewHolder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.foodName.setText(lfoodNames.get(position));
        holder.calorie.setText(lcalories.get(position));
        holder.delete.setImageResource(R.drawable.ic_baseline_remove_circle_24);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteButtons.size()!=0){
                    deleteButtons.remove(position);
                    lfoodNames.remove(position);
                    lcalories.remove(position);
                    notifyItemRemoved(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lfoodNames.size();
    }

    public Context getContext() {
        return context;
    }
    

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView foodName, calorie;
        ImageView delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.tvFoodListName);
            calorie = itemView.findViewById(R.id.tvFoodListCalories);
            delete = itemView.findViewById(R.id.ivFoodListDelete);
        }
    }

    public void swapItems(List<ImageView> list){
        this.deleteButtons = list;
        notifyDataSetChanged();
    }
}
