package com.example.eventlogging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    // declaring firbease auth variable
    private FirebaseAuth mAuth;

    //Declaring variables
    public Toolbar loginToolbar;
    public EditText emailEditText;
    public EditText passwordEditText;
    public Button loginScreenBtn;
    public TextView forgetPassword;
    public ProgressBar loginLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Initializing Variables
        loginToolbar = findViewById(R.id.loginToolbar);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEdittext);
        loginScreenBtn = findViewById(R.id.loginScreenBtn);
        forgetPassword = findViewById(R.id.forgetPassword);
        loginLoading = findViewById(R.id.loginLoading);

        //setSupportActionBar(loginToolbar);
        loginToolbar.setTitle("");

        loginToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Welcome.class);
                startActivity(intent);

                finish();
            }
        });

        //Forget Password TextView Listener
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calling function to go forget password screen
                _gotoForgetPasswordScreen();
            }
        });

        //Login Button Click Listener
        loginScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calling Login Function
                _onLoginCLick();
            }
        });
    }

    //When User will click on forgot your password?
    public void _gotoForgetPasswordScreen (){
        Intent intent = new Intent(getApplicationContext(),ForgetPassword.class);
        startActivity(intent);
    }

    //Declaring Login Button Function
    public void _onLoginCLick () {

        try {
            //Declaring Variables
            String emailText = "";
            String passwordText = "";

            //Getting Text from EditTexts
            emailText = emailEditText.getText().toString();
            passwordText = passwordEditText.getText().toString();

            //Implementing conditions
            if ( emailText.length() == 0 ){
                emailEditText.setError("Email is empty");
            }
            else if( !emailText.contains("@") ){
                emailEditText.setError("Email is not valid");
            }
            else if( !emailText.contains(".") ){
                emailEditText.setError("Email is not valid");
            }
            else if ( passwordText.length() == 0 ) {
                passwordEditText.setError("Password is empty");
            }
            else if( passwordText.length() < 6){
                passwordEditText.setError("Password should contain at least 6 characters");
            }
            else{

                // Set progress visibility to visible
                loginLoading.setVisibility(View.VISIBLE);

                //Calling firebase auth method to signin
                mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        emailEditText.setText("");
                        passwordEditText.setText("");

                        // Set progress visibility to GONE
                        loginLoading.setVisibility(View.GONE);

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

                        // Set progress visibility to GONE
                        loginLoading.setVisibility(View.GONE);
                    }
                });

            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error",
                    Toast.LENGTH_LONG).show();
        }

    }

}