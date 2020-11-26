package com.example.navidoc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.navidoc.Databse.DAO;
import com.example.navidoc.Databse.DatabaseHelper;
import com.example.navidoc.Databse.Doctor;
import com.example.navidoc.Databse.History;
import com.google.android.material.navigation.NavigationView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private AppCompatTextView ambulance, department, floor, doctorsName, officeHours, phoneNumber, websiteUrl;
    private static final String TAG = "DetailActivity";
    private DatabaseHelper db;
    private DAO dao;
    private PlaceRecycleAdapter placeRecycleAdapter;
    private ImageButton navButton;
    private Place places;

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
        navButton = findViewById(R.id.appCompatImageButton);
        db = DatabaseHelper.getInstance(this);
        dao = db.dao();

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        setNavigationListener();

        if (getIntent().hasExtra("selected_place") && getIntent().getParcelableExtra("selected_place") != null)
        {
            setDetails((Place) Objects.requireNonNull(getIntent().getParcelableExtra("selected_place")));
        }

        places = (Place) Objects.requireNonNull(getIntent().getParcelableExtra("selected_place"));

        setWebPageListener();
        setPhoneCallListener();
        setNAvButtonListener();
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
        setDetail(ambulance, R.string.ambulance, place.getAmbulance());
        setDetail(department, R.string.department, place.getDepartment());
        setDetail(floor, R.string.floor, String.valueOf(place.getFloor()));
        setDetail(doctorsName, R.string.doctors_name, place.getDoctorsName());
        setDetail(officeHours, R.string.office_hours, officeHoursText);
        setDetail(phoneNumber, R.string.phone_number, place.getPhoneNumber());
        setDetail(websiteUrl, R.string.website_url, place.getWebsiteUrl());
    }

    private void setDetail(AppCompatTextView editText, int id, String value)
    {
        String text = getResources().getString(id);
        text = text.concat(": ").concat(value);
        editText.setText(value);
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
                case R.id.nav_my_places:
                    Log.d(TAG, "my places");
                    intent = new Intent(this, MyPlacesActivity.class);
                    break;
                case R.id.nav_history:
                    Log.d(TAG, "History");
                    intent = new Intent(this, HistoryActivity.class);
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


    private void setNAvButtonListener()
    {
        navButton.setOnClickListener(view -> {
                createNavigateDialog();
        });
        System.out.println("sad");
    }


    public void createNavigateDialog()
    {
        // Build an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set a title for alert dialog
        builder.setTitle("NaviDoc");


        String navigateTo = "Do you want launch navigation to " + places.getAmbulance() +"("+ places.getDoctorsName() + ")";
        // Ask the final question
        builder.setMessage(navigateTo);

        // Set the alert dialog yes button click listener
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when user clicked the Yes button
                // Set the TextView visibility GONE
                addNewHistory();

                List<Doctor> tmp = dao.getDoctorsByName(places.getDoctorsName());
                if (Objects.requireNonNull(tmp).size() > 0)
                {
                    Doctor doctor = tmp.get(0);
                    doctor.setHistory_id(dao.getLastHistory().getHistory_ID());
                    dao.updatedDoctor(doctor);
                }

            }
        });

        // Set the alert dialog no button click listener
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when No button clicked
                Toast.makeText(getApplicationContext(),
                        "No Button Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    public void addNewHistory()
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        String[] arrSplit = strDate.split(" ");
        String time = arrSplit[1];
        String date1 = arrSplit[0];
        Log.d(TAG, "Datetime: " + date1);
        Log.d(TAG, "Datetime: " + time);
        History history = new History(date1,time);

        dao.insertHistory(history);
    }

}
