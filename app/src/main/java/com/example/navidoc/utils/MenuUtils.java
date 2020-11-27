package com.example.navidoc.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.navidoc.MainActivity;
import com.example.navidoc.R;
import com.example.navidoc.activities.CurrentLocationActivity;
import com.example.navidoc.activities.HistoryActivity;
import com.example.navidoc.activities.MyPlacesActivity;
import com.example.navidoc.activities.PlacesActivity;
import com.google.android.material.navigation.NavigationView;

public class MenuUtils implements NavigationView.OnNavigationItemSelectedListener
{
    private final Context context;
    private final int currentLocation;

    public MenuUtils(Context context, int currentLocation)
    {
        this.context = context;
        this.currentLocation = currentLocation;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        String TAG = "TAG";
        Log.d(TAG, String.valueOf(item.getItemId()));
        Intent intent = null;
        switch (item.getItemId())
        {
            case R.id.nav_home:
                Log.d(TAG, "home");
                intent = (currentLocation == R.id.nav_home) ? null : new Intent(context, MainActivity.class);
                break;
            case R.id.nav_places:
                Log.d(TAG, "places");
                intent = (currentLocation == R.id.nav_places) ? null : new Intent(context, PlacesActivity.class);
                break;
            case R.id.nav_current_location:
                Log.d(TAG, "current location");
                intent = (currentLocation == R.id.nav_current_location) ? null : new Intent(context, CurrentLocationActivity.class);
                break;
            case R.id.nav_my_places:
                Log.d(TAG, "my places");
                intent = (currentLocation == R.id.nav_my_places) ? null : new Intent(context, MyPlacesActivity.class);
                break;
            case R.id.nav_history:
                Log.d(TAG, "History");
                intent = (currentLocation == R.id.nav_history) ? null : new Intent(context, HistoryActivity.class);
                break;
            default:
                Log.d(TAG, "others");
        }

        if (intent != null)
        {
            context.startActivity(intent);
        }
        return false;
    }
}
