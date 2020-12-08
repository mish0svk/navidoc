package com.example.navidoc.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.navidoc.utils.MenuUtils;
import com.example.navidoc.R;
import com.example.navidoc.services.BackgroundScanService;
import com.google.android.material.navigation.NavigationView;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CurrentLocationActivity extends AppCompatActivity
{
    private List<BeaconDevice> beacons;
    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private ImageView searchingImage, btOffImage, foundImage;
    private TextView distance, address, uniqueId, btOffSubtitle, venueName, searchingTitle,
            searchingSubtitle, btOffTitle;
    private static final String TAG = "CurrentLocationActivity";
    private SwipeRefreshLayout refreshLayout;
    private Map<String, String> beaconUniqueIds;
    private LinearLayoutCompat linearLayout;
    private boolean btOn = false;

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
        this.btOffSubtitle = findViewById(R.id.bt_off_subtitle);
        this.venueName = findViewById(R.id.venue_name);
        this.linearLayout = findViewById(R.id.values_layout);
        this.searchingTitle = findViewById(R.id.searching_for_devices_title);
        this.searchingSubtitle = findViewById(R.id.searching_for_devices_subtitle);
        this.searchingImage = findViewById(R.id.searching_for_devices_image);
        this.btOffImage = findViewById(R.id.bt_off_image);
        this.btOffTitle = findViewById(R.id.bt_off_title);
        this.foundImage = findViewById(R.id.found_image);
        beacons = new ArrayList<>();
        setUniqueIds();

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        MenuUtils menuUtils = new MenuUtils(this, R.id.nav_current_location);
        navigationView.setNavigationItemSelectedListener(menuUtils);

        this.refreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "REFRESH ");
            beacons = new ArrayList<>();
            setFoundValuesVisibility(View.INVISIBLE);
            if (btOn)
            {
                setBtOffVisibility(View.INVISIBLE);
                setSearchingVisibility(View.VISIBLE);
            }
            else
            {
                setBtOffVisibility(View.VISIBLE);
                setSearchingVisibility(View.INVISIBLE);
            }

            stopService(serviceIntent);
            startService(serviceIntent);
            refreshLayout.setRefreshing(false);
        });

        this.serviceIntent = BackgroundScanService.createIntent(this);
        setUpBroadcastReceiver();
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            setBtOffVisibility(View.VISIBLE);
            setSearchingVisibility(View.INVISIBLE);
        }
        else
        {
            setBtOffVisibility(View.INVISIBLE);
            setSearchingVisibility(View.VISIBLE);
            startService(serviceIntent);
            btOn = true;
        }

        setFoundValuesVisibility(View.INVISIBLE);
        this.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
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

    private void setFoundValuesVisibility(int visibility)
    {
        this.linearLayout.setVisibility(visibility);
        this.foundImage.setVisibility(visibility);
    }

    private void setBtOffVisibility(int visibility)
    {
        this.btOffImage.setVisibility(visibility);
        this.btOffSubtitle.setVisibility(visibility);
        this.btOffTitle.setVisibility(visibility);
    }

    private void setSearchingVisibility(int visibility)
    {
        this.searchingImage.setVisibility(visibility);
        this.searchingSubtitle.setVisibility(visibility);
        this.searchingTitle.setVisibility(visibility);
    }

    private void setUpBroadcastReceiver()
    {
        this.broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d(TAG, "JEBE ");
                if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction()))
                {
                    onBluetoothAction(intent);

                    return;
                }

                BeaconDevice device = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);
                if (device == null)
                {
                    return;
                }

                removeDevice(device.getAddress());

                if (intent.getAction().equals(BackgroundScanService.DEVICE_DISCOVERED))
                {
                    beacons.add(device);
                }

                displayClosestBeacon();
            }

            private void onBluetoothAction(Intent intent)
            {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF)
                {
                    stopService(serviceIntent);
                    setSearchingVisibility(View.INVISIBLE);
                    setBtOffVisibility(View.VISIBLE);
                    btOn = false;
                }
                else
                {
                    btOn = true;
                    startService(serviceIntent);
                    setSearchingVisibility(View.VISIBLE);
                    setBtOffVisibility(View.INVISIBLE);
                }
                setFoundValuesVisibility(View.INVISIBLE);
            }

            private void removeDevice(String address)
            {
                if (beacons.stream().anyMatch(beacon -> beacon.getAddress().equals(address)))
                {
                    BeaconDevice beaconDevice = beacons.stream().filter(beacon -> beacon.getAddress().equals(address)).collect(Collectors.toList()).get(0);
                    beacons.remove(beaconDevice);
                }
            }
        };
    }

    private void displayClosestBeacon()
    {
        if (!btOn)
        {
            return;
        }

        if (beacons.size() != 0)
        {
            BeaconDevice closestDevice = beacons.get(0);
            for (BeaconDevice beacon: beacons)
            {
                double leftVal = calculateAccuracy(closestDevice.getRssi());
                double rightVal = calculateAccuracy(beacon.getRssi());
                if (leftVal > rightVal)
                {
                    closestDevice = beacon;
                }
            }
            setSearchingVisibility(View.INVISIBLE);
            setBtOffVisibility(View.INVISIBLE);
            setFoundValuesVisibility(View.VISIBLE);

            this.distance.setText(String.valueOf(calculateAccuracy(closestDevice.getRssi())));
            this.address.setText(closestDevice.getAddress());
            if (this.beaconUniqueIds.containsKey(closestDevice.getAddress()))
            {
                this.uniqueId.setText(beaconUniqueIds.get(closestDevice.getAddress()));
            }
            else
            {
                this.uniqueId.setText(R.string.uknown_id);
            }

            double res = Math.pow(10, (double) (-69 - closestDevice.getRssi()) / 20);
            venueName.setText(String.valueOf(res));
        }
        else
        {
            setSearchingVisibility(View.VISIBLE);
            setBtOffVisibility(View.INVISIBLE);
            setFoundValuesVisibility(View.INVISIBLE);
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

    protected static double calculateAccuracy(double rssi)
    {
        return Math.pow(10,  (-69 - rssi) / 20);
    }
}