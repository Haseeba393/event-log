package com.example.eventlogging.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventlogging.EditEvent;
import com.example.eventlogging.EventAdapter;
import com.example.eventlogging.EventModel;
import com.example.eventlogging.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyEvents extends Fragment {

    private FirebaseAuth mAuth;
    private TextView myEventCount;
    private ProgressBar myEventsLoading;
    private ListView myEventsListView;
    private ArrayList<EventModel> events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_events, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initializing firebase authentication
        mAuth = FirebaseAuth.getInstance();

        myEventCount = getActivity().findViewById(R.id.myEventCount);
        myEventsLoading = getActivity().findViewById(R.id.myEventsLoading);
        myEventsListView = getActivity().findViewById(R.id.myEventListView);

        _getMyEvents();

        myEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                // putting event details in intent Extras and going to Edit Event Screen

                Intent intent = new Intent(getActivity(), EditEvent.class);
                intent.putExtra("event_cover", events.get(position).event_cover);
                intent.putExtra("event_title", events.get(position).event_title);
                intent.putExtra("event_description", events.get(position).event_description);
                intent.putExtra("event_location", events.get(position).event_location);
                intent.putExtra("event_date", events.get(position).event_date.split(" ")[0]);
                intent.putExtra("event_time", events.get(position).event_date.split(" ")[1]);
                intent.putExtra("event_person", events.get(position).event_person);
                intent.putExtra("event_number", events.get(position).event_number);
                intent.putExtra("eventUID", events.get(position).eventUID);
                intent.putExtra("owner_id", events.get(position).owner_id);

                startActivity(intent);
            }
        });

    }

    // function to get my events
    public void _getMyEvents () {
        try{
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference("events");

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        final String uid = currentUser.getUid().toString();

                        int EVENTS = (int) dataSnapshot.getChildrenCount();

                        // for loop to get all events
                        // then initilize eventModel object with each event values
                        // then add event model object to Array List
                        // Last Pass that Array list to our customer adapter

                        events = new ArrayList<EventModel>();

                        for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                            // TODO: handle the Event

                            // Getting Children data and saving in hashmap
                            HashMap<String, Object> hashmap = (HashMap<String, Object>) eventSnapshot.getValue();

                            // Getting Values for each property from hashmap and saving in String variables
                            String event_cover = hashmap.get("event_cover").toString();
                            String event_title = hashmap.get("event_title").toString();
                            String event_description = hashmap.get("event_description").toString();
                            String event_location = hashmap.get("event_location").toString();
                            String event_date = hashmap.get("event_date").toString();
                            String event_person = hashmap.get("event_person").toString();
                            String event_number = hashmap.get("event_number").toString();
                            String owner_id = hashmap.get("userUID").toString();
                            String eventUID = eventSnapshot.getKey().toString();

                            // Making EventModel Object and passing event values to it
                            EventModel event = new EventModel(event_cover, event_date, event_description, event_location, event_title, owner_id, eventUID, event_person, event_number);

                            if(owner_id.equals(uid))
                                events.add(event);

                        } // For loop ends here

                        EventAdapter eventAdapter = new EventAdapter(getActivity(), events);
                        myEventsListView.setAdapter(eventAdapter);

                        myEventsLoading.setVisibility(View.GONE);
                        myEventCount.setText("Events: " + String.valueOf(EVENTS));
                        myEventCount.setVisibility(View.VISIBLE);
                        myEventsListView.setVisibility(View.VISIBLE);
                    }
                    catch (Exception e){
                        Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e){
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

}