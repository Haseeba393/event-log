package com.example.eventlogging.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventlogging.Home;
import com.example.eventlogging.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
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
    private Button addEventBtn;
    private Button selectDate;
    private Button selectTime;

    private ProgressBar addEventLoading;
    private int mYear, mMonth, mDay, mHour, mMinute;

    FirebaseStorage storage;

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
        // Initializing firebase Storage
        storage = FirebaseStorage.getInstance();

        eventCover = getActivity().findViewById(R.id.eventCover);
        uploadImgBtn = getActivity().findViewById(R.id.uploadImgBtn);
        removeImgBtn = getActivity().findViewById(R.id.removeImgBtn);
        eventTitle = getActivity().findViewById(R.id.eventTitle);
        eventDescription = getActivity().findViewById(R.id.eventDescription);
        eventLocation = getActivity().findViewById(R.id.eventLocation);
        personName = getActivity().findViewById(R.id.personName);
        personNumber = getActivity().findViewById(R.id.personNumber);
        addEventBtn = getActivity().findViewById(R.id.addEventBtn);
        addEventLoading = getActivity().findViewById(R.id.addEventLoading);

        selectTime=getActivity().findViewById(R.id.selectTime);
        selectDate=getActivity().findViewById(R.id.selectDate);

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

        // Select Date onClick Listener
        selectDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _selectDate();
            }
        });

        // Select Time onClick Listener
        selectTime.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _selectTime();
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

    // Function for selecting Date
    public void _selectDate () {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        selectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    // Function for selecting Time
    public void _selectTime () {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        selectTime.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
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
                eventTitle.setError("Event Title is requried");
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
            else if(selectDate.getText().toString().equals("Select Date")){
                Toast.makeText(getActivity(), "Event Date is required", Toast.LENGTH_LONG).show();
            }
            else if(selectTime.getText().toString().equals("Select Time")){
                Toast.makeText(getActivity(), "Event Time is required", Toast.LENGTH_LONG).show();
            }
            else {

                // Show Loading
                addEventLoading.setVisibility(View.VISIBLE);

                // Creating Database reference to add event details
                FirebaseUser currentUser = mAuth.getCurrentUser();
                final String uid = currentUser.getUid();

                // Create a storage reference from our app
                String storeRef = uid + "/events";
                final StorageReference eventCoverRef = storage.getReference(storeRef);

                // Uploading Event Cover to Storage
                eventCover.setDrawingCacheEnabled(true);
                eventCover.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) eventCover.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Starting Uploading of file
                UploadTask uploadTask = eventCoverRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getActivity(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // after completing file uploading now get its URL
                        eventCoverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Got the download URL for 'users/me/profile.png'

                                // Now put image URI to event data and upload event data to realtime database
                                // Wrinting extra information of user in database
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference myRef = database.getReference("events");


                                String date = selectDate.getText().toString() + " " + selectTime.getText().toString();

                                // Creating Key Value pair for realtime database using hashmap
                                HashMap<String , Object> databaseMap = new HashMap<>();
                                databaseMap.put("event_title",eventTitle.getText().toString().trim());
                                databaseMap.put("event_description",eventDescription.getText().toString().trim());
                                databaseMap.put("event_location",eventLocation.getText().toString().trim());
                                databaseMap.put("event_date",date);
                                databaseMap.put("event_person",personName.getText().toString().trim());
                                databaseMap.put("event_number",personNumber.getText().toString().trim());
                                databaseMap.put("userUID",uid);
                                databaseMap.put("event_cover",uri.toString());

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
                                        selectDate.setText("Select Date");
                                        selectTime.setText("Select Time");


                                        Toast.makeText(getActivity(),"Event is added successfully",Toast.LENGTH_LONG).show();
                                        addEventLoading.setVisibility(View.GONE);
                                        removeImgBtn.setVisibility(View.GONE);
                                        uploadImgBtn.setText(R.string.uploadBtn);


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
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getActivity(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });

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