package com.example.eventlogging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Toolbar signupToolbar;
    public EditText signupFirstName;
    public EditText signupLastName;
    public EditText signupEmail;
    public EditText signupPassword;
    public Button signupScreenBtn;

    public ProgressBar signupLoading;

    //Declaring Variables
    public String firstName = "";
    public String lastName = "";
    public String email = "";
    public String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        signupToolbar = findViewById(R.id.signupToolbar);
        signupFirstName = findViewById(R.id.signupFirstName);
        signupLastName = findViewById(R.id.signupLastName);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupScreenBtn = findViewById(R.id.signupScreenBtn);
        signupLoading = findViewById(R.id.signupLoading);

        signupToolbar.setTitle("");
        signupToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Welcome.class);
                startActivity(intent);
                finish();
            }
        });

        //Signup Button Click Listener
        signupScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onSignupButtonClick();
            }
        });

    }

    //When user will click on signup button
    public void _onSignupButtonClick (){
        try {

            //Getting Text from EditTexts
            firstName = signupFirstName.getText().toString();
            lastName = signupLastName.getText().toString();
            email = signupEmail.getText().toString();
            password = signupPassword.getText().toString();


            //Implementing conditions
            if( firstName.length() == 0 ){
                signupFirstName.setError("First Name is empty");
            }
            else if( lastName.length() == 0 ){
                signupLastName.setError("Last Name is empty");
            }
            else if ( email.length() == 0 ){
                signupEmail.setError("Email is empty");
            }
            else if( !email.contains("@") ){
                signupEmail.setError("Email is not valid");
            }
            else if( !email.contains(".") ){
                signupEmail.setError("Email is not valid");
            }
            else if ( password.length() == 0 ) {
                signupPassword.setError("Password is empty");
            }
            else if( password.length() < 6){
                signupPassword.setError("Password should contain at least 6 characters");
            }
            else{

                signupFirstName.setError("");
                signupLastName.setError("");
                signupEmail.setError("");
                signupPassword.setError("");

                // Show progress bar
                signupLoading.setVisibility(View.VISIBLE);

                //Calling firebase auth method to signin
                mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        String uid = currentUser.getUid();

                        // Wrinting extra information of user in database
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("users/"+uid);

                        // putting data in to hashmap object
                        HashMap<String , Object> databaseMap = new HashMap<>();
                        databaseMap.put("firstname",firstName);
                        databaseMap.put("lastname",lastName);
                        databaseMap.put("email",email);
                        databaseMap.put("uid",uid);

                        // Setting Hashmap in database reference
                        myRef.setValue(databaseMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(getApplicationContext(),"You are successfully signed up",Toast.LENGTH_LONG).show();

                                signupFirstName.setText("");
                                signupLastName.setText("");
                                signupEmail.setText("");
                                signupPassword.setText("");

                                // Hiding progress bar
                                signupLoading.setVisibility(View.GONE);

                                // Moving to home screen
                                Intent intent = new Intent(getApplicationContext(),Home.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                // ending current acitivity
                                finish();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                                // Hiding progress bar
                                signupLoading.setVisibility(View.GONE);
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                        // Hiding progress bar
                        signupLoading.setVisibility(View.GONE);
                    }
                });
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}