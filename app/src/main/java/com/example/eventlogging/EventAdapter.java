package com.example.eventlogging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<EventModel> {
    public EventAdapter(Context context, ArrayList<EventModel> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        EventModel event = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_row, parent, false);
        }

        // Lookup view for data population
        TextView event_title = (TextView) convertView.findViewById(R.id.event_title);
        TextView event_description = (TextView) convertView.findViewById(R.id.event_description);
        TextView event_location = (TextView) convertView.findViewById(R.id.event_location);
        TextView event_date = (TextView) convertView.findViewById(R.id.event_date);
        // Populate the data into the template view using the data object

        event_title.setText(event.event_title);
        event_description.setText(event.event_description);
        event_location.setText(event.event_location);
        event_date.setText(event.event_date);

        // Return the completed view to render on screen
        return convertView;
    }
}

