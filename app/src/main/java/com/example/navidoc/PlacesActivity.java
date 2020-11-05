package com.example.navidoc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class PlacesActivity  extends AppCompatActivity
{
    private List<Place> places = new ArrayList<>();
    private RecyclerView listView;
    private PlaceRecycleAdapter placeRecycleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_places);
        this.listView = findViewById(R.id.scroll_list_places);

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

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
                    Log.d(TAG, "already here");
                    intent = new Intent(this, PlacesActivity.class);
                    break;
                case R.id.nav_current_location:
                    Log.d(TAG, "current location");
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

        setLists();
        renderScrollList();
    }

    private void setLists()
    {
        this.places.add(new Place("Ambulance", "Janko Hrasko", "department1"));
        this.places.add(new Place("Ambulance", "Rocco Rocco", "department2"));
        this.places.add(new Place("Ambulance", "Julia Julia", "department3"));
        this.places.add(new Place("Ambulance", "Philip Philip", "department4"));
        this.places.add(new Place("Ambulance", "Janko Hrasko", "department1"));
        this.places.add(new Place("Ambulance", "Rocco Rocco", "department2"));
        this.places.add(new Place("Ambulance", "Julia Julia", "department3"));
        this.places.add(new Place("Ambulance", "Philip Philip", "department4"));
        this.places.add(new Place("Ambulance", "Janko Hrasko", "department1"));
        this.places.add(new Place("Ambulance", "Rocco Rocco", "department2"));
        this.places.add(new Place("Ambulance", "Julia Julia", "department3"));
        this.places.add(new Place("Ambulance", "Philip Philip", "department4"));
        this.places.add(new Place("Ambulance", "Janko Hrasko", "department1"));
        this.places.add(new Place("Ambulance", "Rocco Rocco", "department2"));
        this.places.add(new Place("Ambulance", "Julia Julia", "department3"));
        this.places.add(new Place("Ambulance", "Philip Philip", "department4"));
    }

    private void renderScrollList()
    {
        this.placeRecycleAdapter = new PlaceRecycleAdapter(this, this.places);
        this.listView = findViewById(R.id.scroll_list_places);
        this.listView.setAdapter(placeRecycleAdapter);
        this.listView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout layout = findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
