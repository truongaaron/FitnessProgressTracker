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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class CompareAdapter extends RecyclerView.Adapter<CompareAdapter.MyViewHolder> {

    static List<String> beforePics, afterPics;
    List<Button> deleteBtns;
    Context context;
    RecyclerView.ViewHolder viewHolder;
    public static int pos1, pos2;
    CompareFragment cf = new CompareFragment();

    public CompareAdapter(Context ct, List<String> beforePics, List<String> afterPics, List<Button> deleteBtns) {
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
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        String beforePic = beforePics.get(position);
        String afterPic = afterPics.get(position);


        // DO THIS IN DIFFERENT CLASS AND STORE DRAWABLES IN HOLDER INSTEAD?
        StorageReference storageReference = firebaseStorage.getReference(firebaseAuth.getUid()).child("Images");
        storageReference.child("Before Pics").child(beforePic).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(holder.before);
            }
        });

        storageReference.child("After Pics").child(afterPic).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(holder.after);
            }
        });

        holder.before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cf.chooseImageFromGallery(1);
                pos1 = position;
            }
        });

        holder.after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cf.chooseImageFromGallery(3);
                pos2 = position;
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String delBeforePic = beforePics.get(position);
                String delAfterPic = afterPics.get(position);

                beforePics.remove(position);
                afterPics.remove(position);
                deleteBtns.remove(position);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, beforePics.size());
                notifyItemRangeChanged(position, afterPics.size());
                notifyItemRangeChanged(position, deleteBtns.size());

                cf.shrinkFirebaseLists(position, delBeforePic, delAfterPic);

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
        }, 1500);
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
