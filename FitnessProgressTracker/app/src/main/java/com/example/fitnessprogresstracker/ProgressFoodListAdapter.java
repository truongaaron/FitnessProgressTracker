package com.example.fitnessprogresstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProgressFoodListAdapter extends RecyclerView.Adapter<ProgressFoodListAdapter.MyViewHolder> {

    List<String> lfoodNames = new ArrayList<>(), lcalories = new ArrayList<>();
    // String foodNames[], calories[];
    Context context;
    RecyclerView.ViewHolder viewHolder;

    public ProgressFoodListAdapter(Context ct, List<String> s1, List<String> s2) {
        context = ct;
        lfoodNames = s1;
        lcalories = s2;
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
    }

    @Override
    public int getItemCount() {
        return lfoodNames.size();
    }

    public Context getContext() {
        return context;
    }

    String mRecentlyDeletedItem = "";
    int mRecentlyDeletedItemPosition = 0;
    public void deleteItem(int position) {
        mRecentlyDeletedItem = lfoodNames.get(position);
        mRecentlyDeletedItemPosition = position;
        lfoodNames.remove(position);
        this.notifyItemRemoved(position);


        mRecentlyDeletedItem = lcalories.get(position);
        mRecentlyDeletedItemPosition = position;
        lcalories.remove(position);
        this.notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView foodName, calorie;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.tvFoodListName);
            calorie = itemView.findViewById(R.id.tvFoodListCalories);
        }
    }
}
