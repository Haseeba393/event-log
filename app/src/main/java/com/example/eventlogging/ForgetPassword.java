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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    // declaring firbease auth variable
    private FirebaseAuth mAuth;

    private Toolbar resetPasswordToolbar;
    public EditText forgetPasswordEmail;
    public Button forgetPasswordBtn;
    public ProgressBar forgetPasswordLoading;

    //Declaring Variables
    String emailText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        resetPasswordToolbar = findViewById(R.id.resetPasswordToolbar);
        forgetPasswordEmail = findViewById(R.id.forgetPasswordEmail);
        forgetPasswordBtn = findViewById(R.id.forgetPasswordBtn);
        forgetPasswordLoading = findViewById(R.id.forgetPasswordLoading);

        forgetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _onSendLinkClick();
            }
        });


        resetPasswordToolbar.setTitle("");
        resetPasswordToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //When User will click on Send Link
    public  void _onSendLinkClick () {

        //Getting Text from EditTexts
        emailText = forgetPasswordEmail.getText().toString();

        //Implementing conditions
        if ( emailText.length() == 0 ){
            forgetPasswordEmail.setError("Email is empty");
        }
        else if( !emailText.contains("@") ){
            forgetPasswordEmail.setError("Email is not valid");
        }
        else if( !emailText.contains(".") ){
            forgetPasswordEmail.setError("Email is not valid");
        }
        else{

            // Show progress bar
            forgetPasswordLoading.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(emailText)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getApplicationContext(), "Password Reset Email has been sent" ,Toast.LENGTH_LONG).show();
                    forgetPasswordEmail.setText("");

                    // hide the progress bar
                    forgetPasswordLoading.setVisibility(View.GONE);

                    Intent intent = new Intent(getApplicationContext(),Login.class);
                    startActivity(intent);

                    // ending current acitivity
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), e.getMessage(),Toast.LENGTH_LONG).show();
                    // hide the progress bar
                    forgetPasswordLoading.setVisibility(View.GONE);
                }
            });
        }
    }
}