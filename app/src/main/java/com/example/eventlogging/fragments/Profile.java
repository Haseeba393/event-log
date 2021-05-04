package com.example.eventlogging.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eventlogging.Home;
import com.example.eventlogging.MainActivity;
import com.example.eventlogging.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Profile extends Fragment {

    // Firebase
    private FirebaseAuth mAuth;

    private LinearLayout profileData;
    private EditText profileFN;
    private EditText profileLN;
    private EditText profileEmail;
    private Button logoutBtn;
    private ProgressBar profileLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Getting user data
        _getUserData();

        // Initializing Variables
        profileData = getActivity().findViewById(R.id.profileData);
        profileFN = getActivity().findViewById(R.id.profileFN);
        profileLN = getActivity().findViewById(R.id.profileLN);
        profileEmail = getActivity().findViewById(R.id.profileEmail);
        logoutBtn = getActivity().findViewById(R.id.logoutBtn);
        profileLoading = getActivity().findViewById(R.id.profileLoading);

        // Logout Click Listener
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calling Logout Function
                _logginOut();
            }
        });

    }

    // Logout Method
    public void _logginOut () {
        mAuth.signOut();

        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        // ending current acitivity
        getActivity().finish();
    }

    // Fetching user data from firebase realtime database
    public void _getUserData () {

        try {
            // Getting User UID
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String UID = firebaseUser.getUid().toString();

            // Making Database reference
            String dbRef = "users/" + UID;
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(dbRef);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    HashMap<String, Object> hashmap = (HashMap<String, Object>) dataSnapshot.getValue();

                    // Populating Profile Fields
                    String fn = hashmap.get("firstname").toString();
                    String ln = hashmap.get("lastname").toString();
                    String em = hashmap.get("email").toString();

                    profileFN.setText(fn);
                    profileLN.setText(ln);
                    profileEmail.setText(em);

                    // Changing visiblilties
                    profileLoading.setVisibility(View.GONE);
                    profileData.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    } // getUserData is ending here
}