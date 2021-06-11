package com.example.eventlogging;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<EventModel> {
    public EventAdapter(Context context, ArrayList<EventModel> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        try{
            // Get the data item for this position
            EventModel event = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_row, parent, false);
            }

            // Lookup view for data population
            ImageView event_cover = (ImageView) convertView.findViewById(R.id.event_cover);
            TextView event_title = (TextView) convertView.findViewById(R.id.event_title);
            TextView event_description = (TextView) convertView.findViewById(R.id.event_description);
            TextView event_location = (TextView) convertView.findViewById(R.id.event_location);
            TextView event_date = (TextView) convertView.findViewById(R.id.event_date);
            // Populate the data into the template view using the data object



            new ImageLoadTask(event.event_cover, event_cover).execute();
            event_title.setText(event.event_title);
            event_title.setText(event.event_title);
            event_description.setText("Description: " + event.event_description);
            event_location.setText("Location: " + event.event_location);
            event_date.setText("Date: " + event.event_date);

            // Return the completed view to render on screen
            return convertView;
        }
        catch (Exception exception) {
            Toast.makeText(getContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
            return null;
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

