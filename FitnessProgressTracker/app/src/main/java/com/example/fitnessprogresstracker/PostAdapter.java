package com.example.fitnessprogresstracker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    List<String> timeStamps, lposts;
    ImageView removeBtn;
    Context context;

    PostFragment pf = new PostFragment();

    public PostAdapter(Context ct, List<String> timeStamps, List<String> posts, ImageView removeBtn) {
        context = ct;
        this.timeStamps = timeStamps;
        this.lposts = posts;
        this.removeBtn = removeBtn;
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
        holder.timeStamp.setText(timeStamps.get(position));
        holder.postContent.setText(lposts.get(position));

        holder.deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parentName = timeStamps.get(position).replaceAll("\\p{Punct}", "");
                String parentNameStr = parentName.substring(5, 13) + " " + parentName.substring(19,25);

                timeStamps.remove(position);
                lposts.remove(position);

                notifyItemRemoved(position);
                notifyItemRangeChanged(position, timeStamps.size());
                notifyItemRangeChanged(position, lposts.size());

                pf.shrinkFirebaseList(parentNameStr);

                delayButtonPress(holder.deletePostBtn);
            }
        });
    }

    private void delayButtonPress(ImageView myButton) {
        myButton.setEnabled(false);
        myButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                myButton.setEnabled(true);
            }
        }, 1000);
    }

    @Override
    public int getItemCount() {
        return timeStamps.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView timeStamp, postContent;
        ImageView deletePostBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStamp = itemView.findViewById(R.id.tvTimeStamp);
            postContent = itemView.findViewById(R.id.etPost);
            deletePostBtn = itemView.findViewById(R.id.ivDeletePost);
        }
    }
}
