package com.example.navidoc.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.simple.SimpleIBeaconListener;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;

import java.util.List;

public class BackgroundScanService extends Service
{
    public static final String DEVICE_DISCOVERED = "DEVICE_DISCOVERED_ACTION";
    public static final String STOP_SERVICE = "STOP_SERVICE_ACTION";
    public static final String EXTRA_DEVICE = "DeviceExtra";
    public static final String EXTRA_DEVICE_COUNT = "DevicesCountExtra";
    public static final String DEVICE_LOST = "DEVICE_LOST_ACTION";

    private ProximityManager proximityManager;
    private boolean isRunning;

    public static Intent createIntent(final Context context) {
        return new Intent(context, BackgroundScanService.class);
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        setupProximityManager();
        this.isRunning = false;
    }

    private void setupProximityManager()
    {
        this.proximityManager = ProximityManagerFactory.create(this);
        this.proximityManager.configuration().scanMode(ScanMode.BALANCED).scanPeriod(ScanPeriod.RANGING)
        .deviceUpdateCallbackInterval(1L);

        this.proximityManager.setIBeaconListener(new SimpleIBeaconListener()
        {
            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> ibeacons, IBeaconRegion region)
            {
              ibeacons.forEach(iBeaconDevice -> onDeviceDiscovered(iBeaconDevice));
            }

            @Override
            public void onIBeaconDiscovered(IBeaconDevice ibeacon, IBeaconRegion region)
            {
               onDeviceDiscovered(ibeacon);
            }

            @Override
            public void onIBeaconLost(IBeaconDevice ibeacon, IBeaconRegion region)
            {
                super.onIBeaconLost(ibeacon, region);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (STOP_SERVICE.equals(intent.getAction()))
        {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (!this.isRunning)
        {
            this.proximityManager.connect(() -> this.proximityManager.startScanning());
            this.isRunning = true;
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy()
    {
        if (this.proximityManager != null)
        {
            this.proximityManager.disconnect();
            this.proximityManager = null;
        }
        super.onDestroy();
    }

    private void onDeviceDiscovered(final RemoteBluetoothDevice device)
    {
        Intent intent = new Intent();
        intent.setAction(DEVICE_DISCOVERED);
        intent.putExtra(EXTRA_DEVICE, device);
        intent.putExtra(EXTRA_DEVICE_COUNT, 0);
        sendBroadcast(intent);
    }

    private void onDeviceLost(final  RemoteBluetoothDevice device)
    {
        Intent intent = new Intent();
        intent.setAction(DEVICE_LOST);
        intent.putExtra(EXTRA_DEVICE, device);
        sendBroadcast(intent);
    }
}