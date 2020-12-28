package com.example.fitnessprogresstracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {

    private EditText searchField;
    private Button searchBtn;
    private RecyclerView usersList;
    private Spinner activity;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    FirebaseRecyclerAdapter<UserSearch, UsersViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        searchField = (EditText) findViewById(R.id.etSearchBar);
        searchBtn = (Button) findViewById(R.id.btnSearch);

        usersList = (RecyclerView) findViewById(R.id.rvSearchResults);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchField.getText().toString();

                firebaseUserSearch(searchText);
                firebaseRecyclerAdapter.startListening();
            }
        });

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = searchField.getText().toString();

                firebaseUserSearch(searchText);
                firebaseRecyclerAdapter.startListening();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void firebaseUserSearch(String searchText) {
        Query query = databaseReference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        usersList.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<UserSearch> options = new FirebaseRecyclerOptions.Builder<UserSearch>().setQuery(query, UserSearch.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<UserSearch, UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull UserSearch model) {
                holder.name.setText(model.getName());

                StorageReference storageReference = firebaseStorage.getReference();
                storageReference.child(model.getImage()).child("Images/Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).fit().centerCrop().into(holder.img);
                    }
                });
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_results_row, parent, false);
                return new UsersViewHolder(view);
            }

        };
        firebaseRecyclerAdapter.startListening();
        usersList.setAdapter(firebaseRecyclerAdapter);
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView name;
        ImageView img;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSearchResultName);
            img = itemView.findViewById(R.id.ivBefore);

            view = itemView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}