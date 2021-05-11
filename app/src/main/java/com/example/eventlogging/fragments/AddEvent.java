package com.example.eventlogging.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventlogging.Home;
import com.example.eventlogging.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddEvent extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private FirebaseAuth mAuth;
    private ImageView eventCover;
    private Button uploadImgBtn;
    private Button removeImgBtn;
    private TextView eventTitle;
    private TextView eventDescription;
    private TextView eventLocation;
    private TextView personName;
    private TextView personNumber;
    private DatePicker eventDate;
    private Button addEventBtn;

    private ProgressBar addEventLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initializing firebase authentication
        mAuth = FirebaseAuth.getInstance();

        eventCover = getActivity().findViewById(R.id.eventCover);
        uploadImgBtn = getActivity().findViewById(R.id.uploadImgBtn);
        removeImgBtn = getActivity().findViewById(R.id.removeImgBtn);
        eventTitle = getActivity().findViewById(R.id.eventTitle);
        eventDescription = getActivity().findViewById(R.id.eventDescription);
        eventLocation = getActivity().findViewById(R.id.eventLocation);
        personName = getActivity().findViewById(R.id.personName);
        personNumber = getActivity().findViewById(R.id.personNumber);
        eventDate = getActivity().findViewById(R.id.eventDate);
        addEventBtn = getActivity().findViewById(R.id.addEventBtn);
        addEventLoading = getActivity().findViewById(R.id.addEventLoading);

        // Setting Minimum Date
        eventDate.setMinDate(System.currentTimeMillis() - 1000);

        // Calling Add new event function on clicking Add EventButton
        addEventBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _submitNewEvent();
            }
        });

        // Making click listener for button
        uploadImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
        });

        // Remove Image from ImageView
        removeImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Change visiblility of remove button to gone
                removeImgBtn.setVisibility(View.GONE);

                // Change Chnage Image text to Upload Image
                uploadImgBtn.setText(R.string.uploadBtn);
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {

            // Checking is permission result is from camera activity
            if (requestCode == MY_CAMERA_PERMISSION_CODE)
            {
                //check if result is granted or not
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Starting Camera
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
                else
                {
                    Toast.makeText(getActivity(), "Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    } // onRequestPermissionsResult Ending here

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
            {
                // Getting Image from result activity
                Bitmap photo = (Bitmap) data.getExtras().get("data");

                // Setting image to image view
                eventCover.setImageBitmap(photo);

                // Visibility Remove Button to visible
                removeImgBtn.setVisibility(View.VISIBLE);

                // Change Upload Image button text to Change Image
                uploadImgBtn.setText(R.string.changeBtn);
            }
        }
        catch (Exception e){
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Submitting new event to realtime database
    public void _submitNewEvent () {

        //Validations

        try {
            if(eventCover.getDrawable() == null) {
                Toast.makeText(getActivity(), "Event Cover is required", Toast.LENGTH_LONG).show();
            }
            else if(eventTitle.getText().toString().length() == 0){
                Toast.makeText(getActivity(), "Event Title is required", Toast.LENGTH_LONG).show();
            }
            else if(eventDescription.getText().toString().length() == 0){
                Toast.makeText(getActivity(), "Event Description is required", Toast.LENGTH_LONG).show();
            }
            else if(eventLocation.getText().toString().length() == 0){
                Toast.makeText(getActivity(), "Event Location is required", Toast.LENGTH_LONG).show();
            }
            else if(personName.getText().toString().length() == 0){
                Toast.makeText(getActivity(), "Person Name is required", Toast.LENGTH_LONG).show();
            }
            else if(personNumber.getText().toString().length() == 0){
                Toast.makeText(getActivity(), "Person Number is required", Toast.LENGTH_LONG).show();
            }
            else {

                // Show Loading
                addEventLoading.setVisibility(View.VISIBLE);

                // Creating Database reference to add event details
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String uid = currentUser.getUid();

                // Wrinting extra information of user in database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("events");

                // creating date string format .e.g. 05-06-1996
                String date = String.valueOf(eventDate.getDayOfMonth()) + "-" + String.valueOf(eventDate.getDayOfMonth()) + "-" + String.valueOf(eventDate.getDayOfMonth());

                // Creating Key Value pair for realtime database using hashmap
                HashMap<String , Object> databaseMap = new HashMap<>();
                databaseMap.put("event_title",eventTitle.getText().toString());
                databaseMap.put("event_description",eventDescription.getText().toString());
                databaseMap.put("event_location",eventLocation.getText().toString());
                databaseMap.put("event_date",date);
                databaseMap.put("userUID",uid);
                databaseMap.put("event_cover","");

                String pushKey = myRef.push().getKey();

                myRef.child(pushKey).setValue(databaseMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // Clear the fields
                        eventCover.setImageDrawable(null);
                        eventTitle.setText("");
                        eventDescription.setText("");
                        eventLocation.setText("");
                        personName.setText("");
                        personNumber.setText("");


                        Toast.makeText(getActivity(),"Event is added successfully",Toast.LENGTH_LONG).show();
                        addEventLoading.setVisibility(View.GONE);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                        addEventLoading.setVisibility(View.GONE);
                    }
                });
            }
        }
        catch (Exception e){
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }


//        if(eventCover == null){
//            Toast.makeText(getActivity(), "Event cover is required", Toast.LENGTH_LONG).show();
//        }
//        else{
//            Toast.makeText(getActivity(), "nextv alidation", Toast.LENGTH_LONG).show();
//        }

    }
}