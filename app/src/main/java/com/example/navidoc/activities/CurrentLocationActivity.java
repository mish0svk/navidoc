package com.example.navidoc.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.navidoc.MainActivity;
import com.example.navidoc.utils.MessageToast;
import com.example.navidoc.R;
import com.example.navidoc.services.BackgroundScanService;
import com.google.android.material.navigation.NavigationView;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CurrentLocationActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private List<BeaconDevice> beacons;
    private TextView distance, address;
    private static final String TAG = "CurrentLocationActivity";
    private static final int REQUEST_CODE_FOR_PERMISSIONS = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        this.navigationView.setCheckedItem(R.id.nav_current_location);
        this.distance = findViewById(R.id.beacon_distance);
        this.address = findViewById(R.id.beacon_address);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        setNavigationListener();

        checkPermissions();

        this.serviceIntent = BackgroundScanService.createIntent(this);
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (beacons == null)
                {
                    beacons = new ArrayList<>();
                }

                BeaconDevice device = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);
                List<BeaconDevice> filteredBeacons = beacons.stream()
                        .filter(beacon -> beacon.getAddress().equals(Objects.requireNonNull(device).getAddress())).collect(Collectors.toList());

                if (filteredBeacons.size() != 0)
                {
                    beacons.remove(filteredBeacons.get(0));
                }

                beacons.add(device);
                displayClosestBeacon();
            }
        };

        startService(serviceIntent);
    }

    private void displayClosestBeacon()
    {
        if (beacons == null || beacons.size() == 0)
        {
            return;
        }

        BeaconDevice device = beacons.get(0);

        for (BeaconDevice beacon: beacons)
        {
            double leftVal = calculateAccuracy(device.getTxPower(), device.getRssi());
            double rightVal = calculateAccuracy(beacon.getTxPower(), beacon.getRssi());

            Log.d(TAG, "displayClosestBeacon: LEFT VAL :" + leftVal + " -> " + device.getAddress());
            Log.d(TAG, "displayClosestBeacon: RIGHT VAL :" + rightVal + " -> " + beacon.getAddress());

            if (leftVal > rightVal )
            {
                device = beacon;
            }
        }

        this.distance.setText(String.valueOf(calculateAccuracy(device.getTxPower(), device.getRssi())));
        this.address.setText(device.getAddress());
    }

    private void checkPermissions()
    {
        List<String> permissions = new ArrayList<>(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.INTERNET,
                Manifest.permission.FOREGROUND_SERVICE));

        permissions.forEach(permission -> {
            int check = ContextCompat.checkSelfPermission(this, permission);
            if (PackageManager.PERMISSION_GRANTED != check)
            {
                ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE_FOR_PERMISSIONS);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (REQUEST_CODE_FOR_PERMISSIONS == requestCode)
            {
                MessageToast.makeToast(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Location permissions are mandatory to use BLE features on Android 6.0 or higher", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void setNavigationListener()
    {
        navigationView.setNavigationItemSelectedListener(item -> {
            Log.d(TAG, String.valueOf(item.getItemId()));
            Intent intent = null;
            switch (item.getItemId())
            {
                case R.id.nav_home:
                    Log.d(TAG, "HOME");
                    stopService(serviceIntent);
                    intent = new Intent(this, MainActivity.class);
                    break;
                case R.id.nav_places:
                    Log.d(TAG, "places");
                    stopService(serviceIntent);
                    intent = new Intent(this, PlacesActivity.class);
                    break;
                case R.id.nav_current_location:
                    Log.d(TAG, "already here");
                    break;
                case R.id.nav_my_places:
                    stopService(serviceIntent);
                    Log.d(TAG, "my places");
                    intent = new Intent(this, MyPlacesActivity.class);
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

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(BackgroundScanService.DEVICE_DISCOVERED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause()
    {
        unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onBackPressed()
    {
        stopService(serviceIntent);
        super.onBackPressed();
    }

    protected static double calculateAccuracy(int txPower, double rssi)
    {
        if (rssi == 0)
        {
            return -1.0;
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0)
        {
            return Math.pow(ratio, 10);
        }
        else
        {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }
}
