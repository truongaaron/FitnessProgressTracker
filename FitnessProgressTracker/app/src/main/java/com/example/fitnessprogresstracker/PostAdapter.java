package com.example.fitnessprogresstracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    String names[], posts[];
    int images[];
    Context context;

    public PostAdapter(Context ct, String s1[], String s2[], int imgs[]) {
        context = ct;
        names = s1;
        posts = s2;
        images = imgs;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(names[position]);
        holder.postContent.setText(posts[position]);
        // holder.profilePic.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, postContent;
        ImageView profilePic;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvFeedUsername);
            postContent = itemView.findViewById(R.id.tvFeedPostContent);
            profilePic = itemView.findViewById(R.id.ivFeedProfilePicture);
        }
    }
}
