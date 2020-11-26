package com.example.navidoc.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.navidoc.adapters.Place;
import com.example.navidoc.R;
import com.example.navidoc.utils.AbstractDialog;
import com.example.navidoc.utils.MenuUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity
{
    private static final String TAG = "DetailActivity";
    private AppCompatTextView ambulance, department, floor, doctorsName, officeHours, phoneNumber, websiteUrl;
    private ImageButton navButton, back;
    private Place place;
    private String activityCalled, query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
        navButton = findViewById(R.id.navigate);
        back = findViewById(R.id.back);

        if (getIntent().hasExtra("activity") && !Objects.requireNonNull(getIntent().getStringExtra("activity")).isEmpty())
        {
            activityCalled = getIntent().getStringExtra("activity");
            query = (Objects.requireNonNull(activityCalled).equals("Places")) ? getIntent().getStringExtra("query") : "";
        }

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        MenuUtils menuUtils = new MenuUtils(this, -1);
        navigationView.setNavigationItemSelectedListener(menuUtils);

        if (getIntent().hasExtra("selected_place") && getIntent().getParcelableExtra("selected_place") != null)
        {
            setDetails(Objects.requireNonNull(getIntent().getParcelableExtra("selected_place")));
        }

        place =  Objects.requireNonNull(getIntent().getParcelableExtra("selected_place"));

        setWebPageListener();
        setPhoneCallListener();
        setButtonsListeners();
    }

    private void setPhoneCallListener()
    {
        phoneNumber.setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 101);
            }

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:+" + phoneNumber.getText().toString().trim()));
            startActivity(callIntent);
        });
    }

    private void setWebPageListener()
    {
        websiteUrl.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(websiteUrl.getText().toString()));
            startActivity(intent);
        });
        System.out.println("sad");
    }

    private void setDetails(Place place)
    {
        //setting string to office hours
        String from = getResources().getString(R.string.from);
        String to = getResources().getString(R.string.to);
        String officeHoursText = from + " " + place.getStartTime() + " " + to + " " + place.getEndTime();

        //setting text to view
        setDetail(ambulance, place.getAmbulance());
        setDetail(department, place.getDepartment());
        setDetail(floor, String.valueOf(place.getFloor()));
        setDetail(doctorsName, place.getDoctorsName());
        setDetail(officeHours, officeHoursText);
        setDetail(phoneNumber, place.getPhoneNumber());
        setDetail(websiteUrl, place.getWebsiteUrl());
    }

    private void setDetail(AppCompatTextView editText, String value)
    {
        editText.setText(value);
    }


    private void setButtonsListeners()
    {
        navButton.setOnClickListener(view -> createNavigateDialog());
        back.setOnClickListener(view ->{
            Intent intent = null;
            switch (activityCalled)
            {
                case "History":
                    intent = new Intent(this, HistoryActivity.class);
                    break;
                case "My places":
                    intent = new Intent(this, MyPlacesActivity.class);
                    break;
                case "Places":
                    intent = new Intent(this, PlacesActivity.class);
                    intent.putExtra("searchInput", query);
                    break;
                default:
                    Log.d(TAG, "setButtonsListeners: SOMETHING IS WRONG");
                    break;
            }

            startActivity(intent);
        });
    }


    public void createNavigateDialog()
    {
        String navigateTo = getResources().getString(R.string.navigate_to) + place.getAmbulance()
                +"("+ place.getDoctorsName() + ")";

        AbstractDialog dialog = AbstractDialog.getInstance();
        dialog.newBuilderInstance(this).setTitle(R.string.app_name).setMessage(navigateTo)
                .sePositiveButton(place).setNegativeButton(this).getBuilder().create().show();
    }
}
