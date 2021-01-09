package com.example.navidoc.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navidoc.services.BackgroundScanService;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
public class Locator
{
    private AppCompatActivity activity;
    private List<BeaconDevice> beacons;
    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver;
    private boolean btOn;

    public Locator(AppCompatActivity activity, Intent serviceIntent)
    {
        this.activity = activity;
        this.serviceIntent = serviceIntent;
        beacons = new ArrayList<>();
        btOn = true;
        setUpBroadcastReceiver();
    }

    private void setUpBroadcastReceiver()
    {
        this.broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
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

            }

            private void onBluetoothAction(Intent intent)
            {
                if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_TURNING_OFF)
                {
                    stopService();
                }
                else
                {
                    startService();
                }
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

    @SuppressLint("DefaultLocale")
    public BeaconDevice displayClosestBeacon()
    {
        if (!btOn)
        {
            return null;
        }

        if (beacons.size() != 0)
        {
            BeaconDevice closestDevice = beacons.get(0);
            for (BeaconDevice beacon : beacons)
            {
                double leftVal = closestDevice.getDistance();
                double rightVal = beacon.getDistance();
                if (leftVal > rightVal)
                {
                    closestDevice = beacon;
                }
            }

            return closestDevice;
        }

        return null;
    }

    public void startService()
    {
        activity.startService(serviceIntent);
        btOn = true;
    }

    public void stopService()
    {
        activity.stopService(serviceIntent);
        btOn = false;
    }

    public void registerReceiver(String action)
    {
        this.activity.registerReceiver(broadcastReceiver, new IntentFilter(action));
    }

    public void unregisterReceiver()
    {
        activity.unregisterReceiver(broadcastReceiver);
    }
}
