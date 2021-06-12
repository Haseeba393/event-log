package com.example.eventlogging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;

public class EditEvent extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    private Toolbar edit_toolbar;

    private FirebaseAuth mAuth;
    private ImageView edit_eventCover;
    private Button edit_uploadImgBtn;
    private Button edit_removeImgBtn;
    private TextView edit_eventTitle;
    private TextView edit_eventDescription;
    private TextView edit_eventLocation;
    private TextView edit_personName;
    private TextView edit_personNumber;
    private Button edit_EventBtn;
    private Button edit_selectDate;
    private Button edit_selectTime;
    private String eventUID;

    private ProgressBar edit_EventLoading;
    private int mYear, mMonth, mDay, mHour, mMinute;

    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // initializing firebase authentication
        mAuth = FirebaseAuth.getInstance();
        // Initializing firebase Storage
        storage = FirebaseStorage.getInstance();

        // Initializing variables with IDs
        edit_toolbar = findViewById(R.id.edit_toolbar);
        edit_eventCover = findViewById(R.id.edit_eventCover);
        edit_uploadImgBtn = findViewById(R.id.edit_uploadImgBtn);
        edit_removeImgBtn = findViewById(R.id.edit_removeImgBtn);
        edit_eventTitle = findViewById(R.id.edit_eventTitle);
        edit_eventDescription = findViewById(R.id.edit_eventDescription);
        edit_eventLocation = findViewById(R.id.edit_eventLocation);
        edit_personName = findViewById(R.id.edit_personName);
        edit_personNumber = findViewById(R.id.edit_personNumber);
        edit_EventBtn = findViewById(R.id.edit_EventBtn);
        edit_EventLoading = findViewById(R.id.edit_EventLoading);

        edit_selectTime = findViewById(R.id.edit_selectTime);
        edit_selectDate = findViewById(R.id.edit_selectDate);

        // Getting Intent for getting extras which we put in MyEvents class
        Intent intent = getIntent();

        // Setting Extras as elements text in Edit Screen

        new ImageLoadTask(intent.getStringExtra("event_cover"), edit_eventCover).execute();
        edit_eventTitle.setText(intent.getStringExtra("event_title"));
        edit_eventDescription.setText(intent.getStringExtra("event_description"));
        edit_eventLocation.setText(intent.getStringExtra("event_location"));
        edit_personName.setText(intent.getStringExtra("event_person"));
        edit_personNumber.setText(intent.getStringExtra("event_number"));
        edit_selectTime.setText(intent.getStringExtra("event_time"));
        edit_selectDate.setText(intent.getStringExtra("event_date"));

        eventUID = intent.getStringExtra("eventUID");

        // Clicking on back button will take user back to home screen
        edit_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Home.class);
                startActivity(intent);

                finish();
            }
        });

        // Calling edit event function
        edit_EventBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _editEvent();
            }
        });

        // Making click listener for button
        edit_uploadImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
        });

        // Select Date onClick Listener
        edit_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editSelectDate();
            }
        });

        edit_selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _editSelectTime();
            }
        });


        // Remove Image from ImageView
        edit_removeImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                edit_eventCover.setImageBitmap(null);
                // Change visiblility of remove button to gone
                edit_removeImgBtn.setVisibility(View.GONE);

                // Change Chnage Image text to Upload Image
                edit_uploadImgBtn.setText(R.string.uploadBtn);
            }
        });

    }

    // Function for selecting Date
    public void _editSelectDate () {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getApplicationContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        edit_selectDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    // Function for selecting Time
    public void _editSelectTime () {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getApplicationContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        edit_selectTime.setText(hourOfDay + ":" + minute);
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
                    Toast.makeText(getApplicationContext(), "Camera Permission Denied", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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
                edit_eventCover.setImageBitmap(photo);

                // Visibility Remove Button to visible
                edit_removeImgBtn.setVisibility(View.VISIBLE);

                // Change Upload Image button text to Change Image
                edit_uploadImgBtn.setText(R.string.changeBtn);
            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // edit event to realtime database
    public void _editEvent () {

        //Validations
        try {
            if(edit_eventCover.getDrawable() == null) {
                Toast.makeText(getApplicationContext(), "Event Cover is required", Toast.LENGTH_LONG).show();
            }
            else if(edit_eventTitle.getText().toString().length() == 0){
                edit_eventTitle.setError("Event Title is requried");
                Toast.makeText(getApplicationContext(), "Event Title is required", Toast.LENGTH_LONG).show();
            }
            else if(edit_eventDescription.getText().toString().length() == 0){
                Toast.makeText(getApplicationContext(), "Event Description is required", Toast.LENGTH_LONG).show();
            }
            else if(edit_eventLocation.getText().toString().length() == 0){
                Toast.makeText(getApplicationContext(), "Event Location is required", Toast.LENGTH_LONG).show();
            }
            else if(edit_personName.getText().toString().length() == 0){
                Toast.makeText(getApplicationContext(), "Person Name is required", Toast.LENGTH_LONG).show();
            }
            else if(edit_personNumber.getText().toString().length() == 0){
                Toast.makeText(getApplicationContext(), "Person Number is required", Toast.LENGTH_LONG).show();
            }
            else if(edit_selectDate.getText().toString().equals("Select Date")){
                Toast.makeText(getApplicationContext(), "Event Date is required", Toast.LENGTH_LONG).show();
            }
            else if(edit_selectTime.getText().toString().equals("Select Time")){
                Toast.makeText(getApplicationContext(), "Event Time is required", Toast.LENGTH_LONG).show();
            }
            else {

                // Show Loading
                edit_EventLoading.setVisibility(View.VISIBLE);

                // Creating Database reference to add event details
                FirebaseUser currentUser = mAuth.getCurrentUser();
                final String uid = currentUser.getUid();

                // Create a storage reference from our app
                String storeRef = uid + "/events";
                final StorageReference eventCoverRef = storage.getReference(storeRef);

                // Uploading Event Cover to Storage
                edit_eventCover.setDrawingCacheEnabled(true);
                edit_eventCover.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) edit_eventCover.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                // Starting Uploading of file
                UploadTask uploadTask = eventCoverRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
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
                                DatabaseReference myRef = database.getReference("events/" + eventUID);


                                String date = edit_selectDate.getText().toString() + " " + edit_selectTime.getText().toString();

                                // Creating Key Value pair for realtime database using hashmap
                                HashMap<String , Object> databaseMap = new HashMap<>();
                                databaseMap.put("event_title",edit_eventTitle.getText().toString().trim());
                                databaseMap.put("event_description",edit_eventDescription.getText().toString().trim());
                                databaseMap.put("event_location",edit_eventLocation.getText().toString().trim());
                                databaseMap.put("event_date",date);
                                databaseMap.put("event_person",edit_personName.getText().toString().trim());
                                databaseMap.put("event_number",edit_personNumber.getText().toString().trim());
                                databaseMap.put("userUID",uid);
                                databaseMap.put("event_cover",uri.toString());

                                String pushKey = myRef.push().getKey();
                                myRef.setValue(databaseMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                // Clear the fields
                                                edit_eventCover.setImageDrawable(null);
                                                edit_eventTitle.setText("");
                                                edit_eventDescription.setText("");
                                                edit_eventLocation.setText("");
                                                edit_personName.setText("");
                                                edit_personNumber.setText("");
                                                edit_selectDate.setText("Select Date");
                                                edit_selectTime.setText("Select Time");


                                                Toast.makeText(getApplicationContext(),"Event is added successfully",Toast.LENGTH_LONG).show();
                                                edit_EventLoading.setVisibility(View.GONE);
                                                edit_removeImgBtn.setVisibility(View.GONE);
                                                edit_uploadImgBtn.setText(R.string.uploadBtn);


                                                Intent intent = new Intent(getApplicationContext(),Home.class);
                                                startActivity(intent);

                                                finish();

                                                Toast.makeText(getApplicationContext(),"Event is updated successfully",Toast.LENGTH_LONG).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                                edit_EventLoading.setVisibility(View.GONE);
                                            }
                                        });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

            }
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // Class for downloading Firebase Image
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}