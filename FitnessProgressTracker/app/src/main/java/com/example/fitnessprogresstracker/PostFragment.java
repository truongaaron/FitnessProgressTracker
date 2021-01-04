package com.example.fitnessprogresstracker;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment<RecylcerView> extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private TextView timeStamp;
    private EditText writePost;
    private Button postBtn;
    private ImageView postProfilePic, deletePost;
    private String timeStampStr, postContent;
    private RecyclerView rvPosts;
    private PostAdapter postAdapter;

    private List<String> timeStamps = new ArrayList<>(), posts = new ArrayList<>();


    private String userNames[], postsArr[];
    int images[] = {};

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Grab User's ID --> get username and profile picture --> when clicking Post, store post in storage

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
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        postProfilePic = view.findViewById(R.id.ivPostProfilePic);
        writePost = view.findViewById(R.id.etWritePost);
        postBtn = view.findViewById(R.id.btnPostToFeed);
        rvPosts = view.findViewById(R.id.rvPosts);
        timeStamp = view.findViewById(R.id.tvTimeStamp);
        deletePost = view.findViewById(R.id.ivDeletePost);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(postProfilePic);
            }
        });

        addExistingPosts();

        postBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(validate()) {
                    addItemsToList();
                    sendUserData();
                    Toast.makeText(getActivity(), postContent, Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        postAdapter = new PostAdapter(getActivity(), timeStamps, posts, deletePost);
        rvPosts.setAdapter(postAdapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public void addExistingPosts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> tempTimeStamps = new ArrayList<>();
                List<String> tempPosts = new ArrayList<>();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    String timeStamp = ds.getKey();
                    String post = ds.child("postContent").getValue(String.class);
                    tempTimeStamps.add(timeStamp.format("Date: %s/%s/%s Time: %s:%s.%s",
                            timeStamp.substring(0, 2),
                            timeStamp.substring(2, 4),
                            timeStamp.substring(4, 8),
                            timeStamp.substring(9, 11),
                            timeStamp.substring(11, 13),
                            timeStamp.substring(13, 15)));
                    tempPosts.add(post);
                }

                timeStamps = new ArrayList<>(tempTimeStamps);
                posts = new ArrayList<>(tempPosts);

                postAdapter = new PostAdapter(getActivity(), timeStamps, posts, deletePost);
                rvPosts.setAdapter(postAdapter);
                rvPosts.setLayoutManager(new LinearLayoutManager(getActivity()));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void shrinkFirebaseList(String parent) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid());
        databaseReference.child("Posts").child(parent).removeValue();
    }

    public boolean validate() {
        postContent = writePost.getText().toString();

        if(postContent.isEmpty()) {
            Toast.makeText(getActivity(), "Post can't be empty! Post must have at least one character.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void addItemsToList() {
        timeStampStr = new SimpleDateFormat("MM.dd.yyyy HH:mm.ss").format(new java.util.Date());
        timeStamps.add(timeStampStr);
        posts.add(writePost.getText().toString());
    }

    private void sendUserData() {
        DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("Posts");
        postsRef = postsRef.child(timeStampStr.replaceAll("\\p{Punct}", ""));
        postsRef.setValue(timeStampStr);

        postsRef = postsRef.child("postContent");
        postsRef.setValue(postContent);


//        UserPost userPost = new UserPost(timeStampStr, postContent);
//        postsRef.setValue(userPost);
    }
}