package com.example.fitnessprogresstracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompareFragment extends Fragment {

    private ImageView addComparisons;
    private Button removeComparisons;
    private static List<ImageView> beforeList = new ArrayList<>(), afterList = new ArrayList<>();
    private List<Button> deleteBtnList = new ArrayList<>();
    private static RecyclerView rvComparisons;
    public static CompareAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri fillerImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CompareFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompareFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompareFragment newInstance(String param1, String param2) {
        CompareFragment fragment = new CompareFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);
        setupUIviews(view);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        addExistingComparisons();

        addComparisons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImagesToList();

                beforeList.get(beforeList.size()-1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gallery = new Intent();
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        getActivity().startActivityForResult(Intent.createChooser(gallery, "Select Image"), 1);
                    }
                });

                afterList.get(afterList.size()-1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent gallery = new Intent();
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        getActivity().startActivityForResult(Intent.createChooser(gallery, "Select Image"), 3);
                    }
                });


                adapter = new CompareAdapter(getActivity(), beforeList, afterList, deleteBtnList);
                rvComparisons.setAdapter(adapter);
                rvComparisons.setLayoutManager(new LinearLayoutManager(getActivity()));;

                delayButtonPress(addComparisons);
            }
        });


        return view;
    }

    private void addExistingComparisons() {
        beforeList.clear();
        afterList.clear();
        deleteBtnList.clear();

        adapter = new CompareAdapter(getActivity(), beforeList, afterList, deleteBtnList);
        rvComparisons.setAdapter(adapter);
        rvComparisons.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseAuth = FirebaseAuth.getInstance();

        StorageReference ref = FirebaseStorage.getInstance().getReference(firebaseAuth.getUid()).child("Images").child("Before Pics");
        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference fileRef : listResult.getItems()) {
                    fillListWithImages();
                    setOnClickListeners(beforeList, 1);
                    setOnClickListeners(afterList, 3);

                }

                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error " + e, Toast.LENGTH_LONG).show();
            }
        });

        Log.d("List Here: ", beforeList.toString());
    }

    public void setOnClickListeners(List<ImageView> list, int requestCode) {
        list.get(list.size()-1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(gallery, "Select Image"), requestCode);
            }
        });
    }

    private void setupUIviews(View view) {
        addComparisons = view.findViewById(R.id.ivAddComparison);
        rvComparisons = view.findViewById(R.id.rvComparisons);
        removeComparisons = view.findViewById(R.id.btnRemoveCompare);
    }

    private void addImagesToList() {
        ImageView fillerImg = new ImageView(getActivity());
        ImageView fillerImg2 = new ImageView(getActivity());
        fillerImg.setImageResource(R.drawable.default_profile_picture);
        fillerImg2.setImageResource(R.drawable.default_profile_picture);
        beforeList.add(fillerImg);
        afterList.add(fillerImg2);
        storeImage(beforeList.size()-1, "Before Pics");
        storeImage(afterList.size()-1, "After Pics");
        deleteBtnList.add(removeComparisons);
        //adapter.notifyDataSetChanged();
    }

    private void fillListWithImages() {
        ImageView fillerImg = new ImageView(getActivity());
        ImageView fillerImg2 = new ImageView(getActivity());
        fillerImg.setImageResource(R.drawable.default_profile_picture);
        fillerImg2.setImageResource(R.drawable.default_profile_picture);
        beforeList.add(fillerImg);
        afterList.add(fillerImg2);
        deleteBtnList.add(removeComparisons);
    }

    public void storeImage(int position, String child) {
        fillerImage = Uri.parse("android.resource://com.example.fitnessprogresstracker/" + R.drawable.default_profile_picture);
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child(child).child(Integer.toString(position));
        UploadTask uploadTask = imageReference.putFile(fillerImage);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void
            onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getActivity(), "Picture Successfully Uploaded.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public CompareAdapter getAdapter() {
        return adapter;
    }

    public List<ImageView> getBeforeList() {
        return beforeList;
    }

    public List<ImageView> getAfterList() { return afterList; }

    public RecyclerView getRV() {
        return rvComparisons;
    }

    private void delayButtonPress(ImageView myButton) {
        myButton.setEnabled(false);
        myButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                myButton.setEnabled(true);
            }
        }, 2500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}