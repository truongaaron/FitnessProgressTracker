package com.example.fitnessprogresstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressFoodListAdapter extends RecyclerView.Adapter<ProgressFoodListAdapter.MyViewHolder> {

    String foodNames[], calories[];
    Context context;

    public ProgressFoodListAdapter(Context ct, String s1[], String s2[]) {
        context = ct;
        foodNames = s1;
        calories = s2;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.progress_food_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(foodNames[position]);
        holder.postContent.setText(calories[position]);
    }

    @Override
    public int getItemCount() {
        return foodNames.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, postContent;
        ImageView profilePic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvFoodListCalories);
            postContent = itemView.findViewById(R.id.tvFoodListName);
            profilePic = itemView.findViewById(R.id.ivFeedProfilePicture);
        }
    }
}
