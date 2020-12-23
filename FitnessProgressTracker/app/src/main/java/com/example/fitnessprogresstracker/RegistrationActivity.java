package com.example.fitnessprogresstracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextInputLayout userName, userPassword, userEmail, userAge, userFeet, userInches, userWeight;
    private RadioGroup maleFemale;
    private EditText inchesChild;
    private Button regButton;
    private TextView userLogin;
    private ImageView userProfilePic;
    private String email, name, password, age = "0", feet, inches, lbs, gender = "m", activityStr, goalStr;
    private double calcActivityLevel;
    private List<String> calorieResults = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private static int PICK_IMAGE = 123;
    Uri imagePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imagePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                userProfilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        inchesChild.setFilters(new InputFilter[]{new InputFilterMinMax(0, 11)});

        Spinner spinner = findViewById(R.id.spinnerRegActivity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(RegistrationActivity.this, R.array.cactivity, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Spinner spinnerGoal = findViewById(R.id.spinnerRegGoal);
        ArrayAdapter<CharSequence> adapterGoal = ArrayAdapter.createFromResource(RegistrationActivity.this, R.array.goals, android.R.layout.simple_spinner_item);
        adapterGoal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(adapterGoal);
        spinnerGoal.setOnItemSelectedListener(this);

        maleFemale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbCalcMale:
                        gender = "m";
                        break;
                    case R.id.rbCalcFemale:
                        gender = "f";
                        break;
                }
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        storageReference = firebaseStorage.getReference();
        storageReference.child("Default_Profile_Picture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imagePath = uri;
                Picasso.get().load(uri).fit().centerCrop().into(userProfilePic);
            }
        });

        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*"); // application/pdf audio/mp3 or *
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age = (userAge.getEditText().getText()).toString();
                feet = (userFeet.getEditText().getText()).toString();
                inches = (userInches.getEditText().getText()).toString();
                lbs = (userWeight.getEditText().getText()).toString();

                if(validate()) {
                    String user_email = (userEmail.getEditText().getText()).toString().trim();
                    String user_password = (userPassword.getEditText().getText()).toString().trim();

                    calculateBMR(age, feet, inches, lbs);
                    setGoal(goalStr);

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                sendEmailVerification();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            }
        });

    }

    private void setupUIViews() {
        userName = (TextInputLayout) findViewById(R.id.tilRegUserName);
        userEmail = (TextInputLayout) findViewById(R.id.tilRegUserEmail);
        userPassword = (TextInputLayout) findViewById(R.id.tilRegUserPassword);
        userAge = (TextInputLayout) findViewById(R.id.tilRegAge);
        userFeet = (TextInputLayout) findViewById(R.id.tilRegFeet);
        userInches= (TextInputLayout) findViewById(R.id.tilRegInches);
        userWeight = (TextInputLayout) findViewById(R.id.tilRegWeight);
        regButton = (Button) findViewById(R.id.btnRegister);
        userLogin = (TextView) findViewById(R.id.tvRegUserLogin);
        maleFemale = (RadioGroup) findViewById(R.id.rgRegMaleFemale);
        userProfilePic = (ImageView) findViewById(R.id.ivRegProfile);
        inchesChild = (EditText) findViewById(R.id.tilRegInchesChild);
    }

    private Boolean validate() {
        Boolean result = false;

        name = (userName.getEditText().getText()).toString();
        password = (userPassword.getEditText().getText()).toString();
        email = (userEmail.getEditText().getText()).toString();
        age = (userAge.getEditText().getText()).toString();
        feet = (userFeet.getEditText().getText()).toString();
        inches = (userInches.getEditText().getText()).toString();
        lbs = (userWeight.getEditText().getText()).toString();


        if(name.isEmpty() || password.isEmpty() || email.isEmpty() || age.isEmpty() || feet.isEmpty() || inches.isEmpty() | lbs.isEmpty()) {
            Toast.makeText(this, "Please enter all the details", Toast.LENGTH_SHORT).show();
        } else {
            result = true;
        }

        return result;
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered, Verification email sent!", Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Registration Failed. Verification email not sent.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic"); // User Id/Images/profile_pic.png
        UploadTask uploadTask = imageReference.putFile(imagePath);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationActivity.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void
            onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(RegistrationActivity.this, "Profile Picture Successfully Updated.", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference users = firebaseDatabase.getReference("Users").child(firebaseAuth.getUid());

        UserSearch userSearch = new UserSearch(name, firebaseAuth.getUid());
        users.setValue(userSearch);


        UserProfile userProfile = new UserProfile(age, email, name, goalStr);
        myRef.setValue(userProfile);
    }

    public double calculateBMR(String age, String feet, String inches, String lbs) {
        double bmr = 0;
        int bmrFinal = 0;
        int ageInt = Integer.parseInt(age);

        if(gender.equals("m")) {
            bmr = Math.ceil((10*lbs_to_kg(lbs)) + (6.25*(ft_to_cm(feet) + in_to_cm(inches))) - (5 * ageInt) + 5) * calcActivityLevel;
        } else {
            bmr = Math.ceil((10*lbs_to_kg(lbs)) + (6.25*(ft_to_cm(feet) + in_to_cm(inches))) - (5 * ageInt) - 161) * calcActivityLevel;
        }
        bmrFinal = (int) bmr;

        calorieResults.add(Integer.toString(bmrFinal));
        calorieResults.add(Integer.toString(bmrFinal - 300));
        calorieResults.add(Integer.toString(bmrFinal - 500));
        calorieResults.add(Integer.toString(bmrFinal - 1000));

        return bmrFinal;
    }

    public double ft_to_cm(String feet) {
        int ft = Integer.parseInt(feet);
        return ft * 30.48;
    }

    public double in_to_cm(String inch) {
        int in = Integer.parseInt(inch);
        return in * 2.54;
    }

    public double lbs_to_kg(String lbs) {
        int lb = Integer.parseInt(lbs);
        return lb * 0.453592;
    }

    public void setActivityLevel(String activityStr) {
        double[] activityLevels = {1, 1.2, 1.375, 1.465, 1.55, 1.75, 1.9};
        calcActivityLevel = activityLevels[Integer.parseInt(activityStr)];
    }

    public void setGoal(String goal) {
        goalStr = calorieResults.get(Integer.parseInt(goal));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
          switch(parent.getId()) {
              case R.id.spinnerRegActivity:
                  activityStr = Integer.toString(position);
                  setActivityLevel(activityStr);
                  break;
              case R.id.spinnerRegGoal:
                  goalStr = Integer.toString(position);;
                  break;
          }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}