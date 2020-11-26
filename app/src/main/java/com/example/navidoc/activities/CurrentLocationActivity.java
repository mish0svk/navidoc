package com.example.navidoc.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.navidoc.utils.MenuUtils;
import com.example.navidoc.R;
import com.example.navidoc.services.BackgroundScanService;
import com.google.android.material.navigation.NavigationView;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CurrentLocationActivity extends AppCompatActivity
{
    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private BeaconDevice closestBeaconDevice;
    private TextView distance, address, uniqueId, noBeacons;
    private static final String TAG = "CurrentLocationActivity";
    private SwipeRefreshLayout refreshLayout;
    private final Handler handler= new Handler(Looper.getMainLooper());
    private static final int DURATION_TIME_S = 20;
    private String lastUpdatedTime = "";
    private SimpleDateFormat formatter;
    private Map<String, String> beaconUniqueIds;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_current_location);
        this.distance = findViewById(R.id.beacon_distance);
        this.address = findViewById(R.id.beacon_address);
        this.refreshLayout = findViewById(R.id.swipe_up_to_refresh);
        this.uniqueId = findViewById(R.id.unique_id);
        this.noBeacons = findViewById(R.id.no_beacons);
        this.formatter = new SimpleDateFormat("HH:mm:ss");
        setUniqueIds();

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        MenuUtils menuUtils = new MenuUtils(this, R.id.nav_current_location);
        navigationView.setNavigationItemSelectedListener(menuUtils);

        this.refreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "REFRESH ");
            stopService(serviceIntent);
            startService(serviceIntent);
            refreshLayout.setRefreshing(false);
        });

        this.serviceIntent = BackgroundScanService.createIntent(this);
        setUpBroadcastReceiver();

        startService(serviceIntent);

        this.handler.postDelayed(this::calculateTime, DURATION_TIME_S * 1000);
    }

    private void setUniqueIds()
    {
        if (this.beaconUniqueIds == null)
        {
            this.beaconUniqueIds = new HashMap<>();
        }

        this.beaconUniqueIds.put("C2:7E:8A:C4:27:2F", "UuaJiX");
        this.beaconUniqueIds.put("FC:74:76:7C:5F:2E", "UutvWt");
        this.beaconUniqueIds.put("FB:5A:65:C8:66:B5", "Uujp66");
        this.beaconUniqueIds.put("C4:A6:40:07:67:FD", "UuGhGx");
        this.beaconUniqueIds.put("F3:26:2A:C2:DD:2B", "UuehLL");
    }

    private void calculateTime()
    {
        if (lastUpdatedTime.isEmpty())
        {
            noBeacons.setVisibility(View.VISIBLE);
            distance.setVisibility(View.INVISIBLE);
            address.setVisibility(View.INVISIBLE);
            uniqueId.setVisibility(View.INVISIBLE);
        }
        else
        {
            String newTime = formatter.format(Calendar.getInstance().getTime());
            Duration difference = Duration.between(LocalTime.parse(lastUpdatedTime), LocalTime.parse(newTime));
            if (difference.getSeconds() > DURATION_TIME_S)
            {
                noBeacons.setVisibility(View.VISIBLE);
                distance.setVisibility(View.INVISIBLE);
                address.setVisibility(View.INVISIBLE);
                uniqueId.setVisibility(View.INVISIBLE);
            }
        }

        this.handler.postDelayed(this::calculateTime, DURATION_TIME_S * 1000);
    }

    private void setUpBroadcastReceiver()
    {
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                //no beacon device so far
                if (closestBeaconDevice == null)
                {
                    closestBeaconDevice = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);
                    displayClosestBeacon();
                    return;
                }

                BeaconDevice device = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);

                if (device == null)
                {
                    return;
                }


                double leftVal = calculateAccuracy(closestBeaconDevice.getTxPower(), closestBeaconDevice.getRssi());
                double rightVal = calculateAccuracy(device.getTxPower(), device.getRssi());

                Log.d(TAG, "displayClosestBeacon: LEFT VAL :" + leftVal + " -> " + closestBeaconDevice.getAddress());
                Log.d(TAG, "displayClosestBeacon: RIGHT VAL :" + rightVal + " -> " + device.getAddress());

                //if same beacon or distance is closer
                if (closestBeaconDevice.getAddress().equals(device.getAddress()) || leftVal > rightVal)
                {
                    closestBeaconDevice = device;
                }
                displayClosestBeacon();
            }
        };
    }

    private void displayClosestBeacon()
    {
        if (closestBeaconDevice != null)
        {
            noBeacons.setVisibility(View.INVISIBLE);
            distance.setVisibility(View.VISIBLE);
            address.setVisibility(View.VISIBLE);
            uniqueId.setVisibility(View.VISIBLE);

            this.lastUpdatedTime = formatter.format(Calendar.getInstance().getTime());
            this.distance.setText(String.valueOf(calculateAccuracy(closestBeaconDevice.getTxPower(), closestBeaconDevice.getRssi())));
            this.address.setText(closestBeaconDevice.getAddress());
            if (this.beaconUniqueIds.containsKey(closestBeaconDevice.getAddress()))
            {
                this.uniqueId.setText(beaconUniqueIds.get(closestBeaconDevice.getAddress()));
            }
            else
            {
                this.uniqueId.setText(R.string.uknown_id);
            }
        }
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
