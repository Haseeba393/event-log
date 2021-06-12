package com.example.eventlogging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.eventlogging.fragments.AddEvent;
import com.example.eventlogging.fragments.Events;
import com.example.eventlogging.fragments.MyEvents;
import com.example.eventlogging.fragments.Profile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Setting Default Screen
        toolbar.setTitle("Events");
        loadFragment(new Events());

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_events:
                        toolbar.setTitle("Events");
                        fragment = new Events();
                        loadFragment(fragment);
                        return true;
                    case R.id.navigation_addevent:
                        toolbar.setTitle("Add Event");
                        fragment = new AddEvent();
                        loadFragment(fragment);
                        return true;

                    case R.id.navigation_myevents:
                        toolbar.setTitle("My Events");
                        fragment = new MyEvents();
                        loadFragment(fragment);
                        return true;

                    case R.id.navigation_profile:
                        toolbar.setTitle("Profile");
                        fragment = new Profile();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }


    // Loading Fragment
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}