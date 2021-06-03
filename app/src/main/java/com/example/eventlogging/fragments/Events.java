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

public class Events extends Fragment {

    private TextView eventCount;
    private ProgressBar eventsLoading;
    private ListView eventsListView;

    private String dummyEvents [] = {"Event 1", "Event 2", "Event 3"};

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

                    int EVENTS = (int) dataSnapshot.getChildrenCount();

                    try {

                        EventModel event1 = new EventModel("20-5-2021","description", "FSD", "EventTitle");
                        EventModel event2 = new EventModel("22-10-2022","description 2", "LHR", "Event title 2");

                        //EventModel [] events = {event1, event2};

                        ArrayList<EventModel> events = new ArrayList<EventModel>();
                        events.add(event1);
                        events.add(event2);

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