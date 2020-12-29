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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    final Context context = this;
    Uri imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

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

        CompareFragment cf = new CompareFragment();

        if(requestCode == 1 && resultCode == RESULT_OK && data.getData() != null) {
            Log.d("entered: ", "one");
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                cf.getBeforeList().get(cf.getBeforeList().size()-1).setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == 3 && resultCode == RESULT_OK && data.getData() != null) {
            Log.d("entered: ", "three");
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                cf.getAfterList().get(cf.getAfterList().size()-1).setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cf.adapter.notifyDataSetChanged();
    }


}