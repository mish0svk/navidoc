package com.example.navidoc.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.navidoc.adapters.Place;
import com.example.navidoc.R;
import com.example.navidoc.services.BackgroundScanService;
import com.example.navidoc.utils.AbstractDialog;
import com.example.navidoc.utils.BeaconUtility;
import com.example.navidoc.utils.Locator;
import com.example.navidoc.utils.MenuUtils;
import com.example.navidoc.utils.MessageToast;
import com.google.android.material.navigation.NavigationView;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity
{
    private static final String TAG = "DetailActivity";
    private AppCompatTextView ambulance, department, floor, doctorsName, officeHours, phoneNumber, websiteUrl;
    private ImageButton navButton, back;
    private Place place;
    private String activityCalled, query;
    private Locator locator;

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

        Intent serviceIntent = BackgroundScanService.createIntent(this);
        locator = new Locator(this, serviceIntent);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            MessageToast.makeToast(this, R.string.bluetooth_is_off, Toast.LENGTH_SHORT).show();
        }
        else
        {
            locator.startService();
        }

        locator.registerReceiver(BluetoothAdapter.ACTION_STATE_CHANGED);
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
        BeaconDevice beacon = locator.displayClosestBeacon();
        if (beacon == null || BeaconUtility.getUniqueId(beacon) == null)
        {
            MessageToast.makeToast(this, R.string.unavailable_location, Toast.LENGTH_SHORT).show();
            return;
        }

        String navigateTo = getResources().getString(R.string.navigate_to) + place.getAmbulance()
                +"("+ place.getDoctorsName() + ")";

        AbstractDialog dialog = AbstractDialog.getInstance();
        dialog.newBuilderInstance(this).setTitle(R.string.app_name).setMessage(navigateTo)
                .sePositiveButton(BeaconUtility.getUniqueId(beacon), place).setNegativeButton(this).getBuilder().create().show();
    }

    @Override
    public void onBackPressed()
    {
        locator.stopService();
        super.onBackPressed();
    }

    @Override
    protected void onResume()
    {
        if (locator != null)
        {
            locator.registerReceiver(BackgroundScanService.DEVICE_DISCOVERED);
        }
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        locator.unregisterReceiver();
        super.onPause();
    }
}
