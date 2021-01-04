package com.example.fitnessprogresstracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Base64;

public class SecondActivity extends AppCompatActivity {

    final Context context = this;
    Uri imagePath;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    CompareFragment cf = new CompareFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.searchMenu: {
                startActivity(new Intent(SecondActivity.this, SearchActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());

        if(requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();

            removeFromStorage("Before Pics", cf.getBeforeList().get(cf.getAdapter().pos1));
            cf.getBeforeList().set(cf.getAdapter().pos1, timeStamp);
            uploadImageToStorage(imagePath, "Before Pics", timeStamp);
        }

        if(requestCode == 3 && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();

            removeFromStorage("After Pics", cf.getAfterList().get(cf.getAdapter().pos2));
            cf.getAfterList().set(cf.getAdapter().pos2, timeStamp);
            uploadImageToStorage(imagePath, "After Pics", timeStamp);
        }

        updateDatabase();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                cf.adapter.notifyDataSetChanged();

            }
        }, 5000);


    }

    public void updateDatabase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userBeforeList");
        dbRef.setValue(cf.getBeforeList());

        dbRef = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("userAfterList");
        dbRef.setValue(cf.getAfterList());
    }

    public void uploadImageToStorage(Uri uri, String childName, String uniqueID) {
        Toast.makeText(SecondActivity.this, "Uploading Image...", Toast.LENGTH_LONG).show();
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child(childName).child(uniqueID); // User Id/Images/profile_pic.png
        UploadTask uploadTask = imageReference.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Fail: ", e.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("Success: ", "File successfully uploaded to storage.");
            }
        });
    }

    private void removeFromStorage(String childName, String uniqueID) {
        StorageReference storage = firebaseStorage.getReference(firebaseAuth.getUid()).child("Images").child(childName).child(uniqueID);
        storage.delete();
    }

}