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
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventlogging.R;

public class AddEvent extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private ImageView eventCover;
    private Button uploadImgBtn;
    private Button removeImgBtn;
    private TextView eventTitle;
    private TextView eventDescription;
    private DatePicker eventDate;
    private Button addEventBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eventCover = getActivity().findViewById(R.id.eventCover);
        uploadImgBtn = getActivity().findViewById(R.id.uploadImgBtn);
        removeImgBtn = getActivity().findViewById(R.id.removeImgBtn);
        eventTitle = getActivity().findViewById(R.id.eventTitle);
        eventDescription = getActivity().findViewById(R.id.eventDescription);
        eventDate = getActivity().findViewById(R.id.eventDate);
        addEventBtn = getActivity().findViewById(R.id.addEventBtn);

        // Setting Minimum Date
        eventDate.setMinDate(System.currentTimeMillis() - 1000);

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
                // Setting Upload icon to imageview
                Drawable drawable = getResources().getDrawable(R.drawable.upload);
                eventCover.setImageDrawable(drawable);

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
}