package com.example.eventlogging.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventlogging.EventAdapter;
import com.example.eventlogging.EventModel;
import com.example.eventlogging.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Events extends Fragment {

    private TextView eventCount;
    private ProgressBar eventsLoading;
    private ListView eventsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        eventCount = getActivity().findViewById(R.id.eventCount);
        eventsLoading = getActivity().findViewById(R.id.eventsLoading);
        eventsListView = getActivity().findViewById(R.id.eventListView);

        _getEvents();
    }

    // function to get events
    public void _getEvents () {
        try{
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference myRef = firebaseDatabase.getReference("events");

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    try {

                        int EVENTS = (int) dataSnapshot.getChildrenCount();

                        // for loop to get all events
                        // then initilize eventModel object with each event values
                        // then add event model object to Array List
                        // Last Pass that Array list to our customer adapter

                        ArrayList<EventModel> events = new ArrayList<EventModel>();

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
                            String owner_id = hashmap.get("userUID").toString();

                            // Making EventModel Object and passing event values to it
                            EventModel event = new EventModel(event_cover, event_date, event_description, event_location, event_title, owner_id);

                            events.add(event);

                        } // For loop ends here

                        EventAdapter eventAdapter = new EventAdapter(getActivity(), events);
                        eventsListView.setAdapter(eventAdapter);

                        eventsLoading.setVisibility(View.GONE);
                        eventCount.setText("Events: " + String.valueOf(EVENTS));
                        eventCount.setVisibility(View.VISIBLE);
                        eventsListView.setVisibility(View.VISIBLE);
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