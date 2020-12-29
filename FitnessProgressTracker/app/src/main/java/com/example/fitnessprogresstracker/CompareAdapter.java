package com.example.fitnessprogresstracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.MyViewHolder> {

    List<ImageView> beforePics, afterPics;
    List<Button> deleteBtns;
    Context context;
    RecyclerView.ViewHolder viewHolder;
    public static int pos1, pos2;

    public CompareAdapter(Context ct, List<ImageView> beforePics, List<ImageView> afterPics, List<Button> deleteBtns) {
        context = ct;
        this.beforePics = beforePics;
        this.afterPics = afterPics;
        this.deleteBtns = deleteBtns;
    }

    @NonNull
    @Override
    public CompareAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.compare_row, parent, false);
        context = parent.getContext();

        viewHolder = new CompareAdapter.MyViewHolder(view);
        return new CompareAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompareAdapter.MyViewHolder holder, int position) {
        holder.before.setImageDrawable(beforePics.get(position).getDrawable());
        holder.before.bringToFront();
        holder.before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforePics.get(position).performClick();
                pos1 = position;
            }
        });

        holder.after.setImageDrawable(afterPics.get(position).getDrawable());
        holder.after.bringToFront();
        holder.after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterPics.get(position).performClick();
                pos2 = position;
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforePics.remove(position);
                afterPics.remove(position);
                deleteBtns.remove(position);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, beforePics.size());
                notifyItemRangeChanged(position, afterPics.size());
                notifyItemRangeChanged(position, deleteBtns.size());

                delayButtonPress(holder.delete);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beforePics.size();
    }

    public Context getContext() {
        return context;
    }

    private void delayButtonPress(Button myButton) {
        myButton.setEnabled(false);
        myButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                myButton.setEnabled(true);
            }
        }, 1000);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView before, after;
        Button delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            before = itemView.findViewById(R.id.ivBefore);
            after = itemView.findViewById(R.id.ivAfter);
            delete = itemView.findViewById(R.id.btnRemoveCompare);
        }
    }


}
