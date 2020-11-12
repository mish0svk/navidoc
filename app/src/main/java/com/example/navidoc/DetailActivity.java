package com.example.navidoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private AppCompatTextView ambulance, department, floor, doctorsName, officeHours, phoneNumber, websiteUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        ambulance = findViewById(R.id.ambulance);
        department = findViewById(R.id.department);
        floor = findViewById(R.id.floor);
        officeHours = findViewById(R.id.office_hours);
        phoneNumber = findViewById(R.id.phone_number);
        websiteUrl = findViewById(R.id.website_url);
        doctorsName = findViewById(R.id.doctors_name);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        setNavigationListener();

        if (getIntent().hasExtra("selected_place") && getIntent().getParcelableExtra("selected_place") != null)
        {
            setDetails((Place) Objects.requireNonNull(getIntent().getParcelableExtra("selected_place")));
        }
    }

    private void setDetails(Place place)
    {
        //setting string to office hours
        String from = getResources().getString(R.string.from);
        String to = getResources().getString(R.string.to);
        String officeHoursText = from + " " + place.getStartTime() + " " + to + " " + place.getEndTime();

        //setting text to view
        setDetail(ambulance, R.string.ambulance, place.getAmbulance());
        setDetail(department, R.string.department, place.getDepartment());
        setDetail(floor, R.string.floor, String.valueOf(place.getFloor()));
        setDetail(doctorsName, R.string.doctors_name, place.getDoctorsName());
        setDetail(officeHours, R.string.office_hours, officeHoursText);
        setDetail(phoneNumber, R.string.phone_number, place.getPhoneNumber());
        setDetail(websiteUrl, R.string.website_url, place.getWebsiteUrl());
    }

    private void setDetail(AppCompatTextView textView, int id, String value)
    {
        String text = getResources().getString(id);
        text = text.concat(": ").concat(value);
        textView.setText(text);
    }


    @SuppressLint("NonConstantResourceId")
    private void setNavigationListener()
    {
        navigationView.setNavigationItemSelectedListener(item -> {
            String TAG = "TAG";
            Log.d(TAG, String.valueOf(item.getItemId()));
            Intent intent = null;
            switch (item.getItemId())
            {
                case R.id.nav_home:
                    Log.d(TAG, "HOME");
                    intent = new Intent(this, MainActivity.class);
                    break;
                case R.id.nav_places:
                    Log.d(TAG, "places");
                    intent = new Intent(this, PlacesActivity.class);
                    break;
                case R.id.nav_current_location:
                    Log.d(TAG, "current location");
                    break;
                default:
                    Log.d(TAG, "others");
            }

            if (intent != null)
            {
                startActivity(intent);
            }

            return false;
        });
    }
}
