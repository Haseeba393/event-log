package com.example.eventlogging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class Welcome extends AppCompatActivity {

    private Button loginButton;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calling Login Function
                _gotoLoginScreen();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _gotoSignupScreen();
            }
        });
    }

    //Function that will take user to Login screen
    public void _gotoLoginScreen () {

        //Declaring Intent
        Intent intent = new Intent(getApplicationContext(), Login.class);

        //Starting Intent
        startActivity(intent);

    }

    //Function that will take user to Signup screen
    public void _gotoSignupScreen () {

        //Declaring Intent
        Intent intent = new Intent(getApplicationContext(), Signup.class);

        //Starting Intent
        startActivity(intent);

    }

}