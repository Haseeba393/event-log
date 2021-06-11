package com.example.eventlogging;

// Declaring Class for the Event Model which will be used for the Custom ListView Adapter
public class EventModel {
    public String event_cover;
    public String event_date;
    public String event_description;
    public String event_location;
    public String event_title;
    public String owner_id;

    // Parameterized Constructor
    // A Constructor in which we are passing parameters to initialize the Class Object
    public EventModel(String event_cover, String event_date, String event_description, String event_location, String event_title, String owner_id) {
        this.event_cover = event_cover;
        this.event_date = event_date;
        this.event_description = event_description;
        this.event_location = event_location;
        this.event_title = event_title;
        this.owner_id = owner_id;
    }
}
