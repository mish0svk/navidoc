package com.example.navidoc.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.navidoc.MainActivity;
import com.example.navidoc.utils.MessageToast;
import com.example.navidoc.R;
import com.example.navidoc.services.BackgroundScanService;
import com.google.android.material.navigation.NavigationView;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurrentLocationActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private BeaconDevice closestBeaconDevice;
    private TextView distance, address, uniqueId, noBeacons;
    private static final String TAG = "CurrentLocationActivity";
    private static final int REQUEST_CODE_FOR_PERMISSIONS = 100;
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
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        this.navigationView.setCheckedItem(R.id.nav_current_location);
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
        setNavigationListener();

        this.refreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "REFRESH ");
            stopService(serviceIntent);
            startService(serviceIntent);
            refreshLayout.setRefreshing(false);
        });

        checkPermissions();

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
