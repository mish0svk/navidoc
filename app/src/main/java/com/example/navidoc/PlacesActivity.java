package com.example.navidoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.navidoc.Databse.DAO;
import com.example.navidoc.Databse.DatabaseHelper;
import com.example.navidoc.Databse.Department;
import com.example.navidoc.Databse.Doctor;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlacesActivity  extends AppCompatActivity implements OnPlaceListener
{
    private List<Place> places = new ArrayList<>();
    private RecyclerView listView;
    private static final String TAG = "PlacesActivity";
    private PlaceRecycleAdapter placeRecycleAdapter;
    private NavigationView navigationView;
    private AutoCompleteTextView searchField;
    private DatabaseHelper db;
    private DAO dao;
    private ImageButton submitSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_places);
        this.listView = findViewById(R.id.scroll_list_places);
        searchField = findViewById(R.id.search_field);
        submitSearch = findViewById(R.id.submit_search);

        if (getIntent().hasExtra("searchInput") && !getIntent().getStringExtra("searchInput").isEmpty())
        {
            searchField.setText(getIntent().getStringExtra("searchInput"));
        }

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        setSubmitSearchListener();
        setNavigationListener();

        db = DatabaseHelper.getInstance(this);
        dao = db.dao();

        searchRelevantData();
        renderScrollList();

        if (!searchField.getText().toString().isEmpty())
        {
            readDataFromDb(searchField.getText().toString());
        }
    }

    private void setSubmitSearchListener()
    {
        submitSearch.setOnClickListener(view -> {
            readDataFromDb(searchField.getText().toString());
        });
    }

    private void searchRelevantData()
    {
        List<Place> places = new ArrayList<>();
        PlaceSearchAdapter placeSearchAdapter = new PlaceSearchAdapter(getApplicationContext(), places);
        searchField.setThreshold(1);
        searchField.setAdapter(placeSearchAdapter);
        searchField.setOnItemClickListener((parent, view, position, id) -> {
            readDataFromDb(searchField.getText().toString());
        });
    }

    private void readDataFromDb(String query)
    {
        List<Doctor> doctors = new ArrayList<>();

        if (!query.isEmpty())
        {
            doctors.addAll(dao.getDoctorsByAmbulanceName(query));
            doctors.addAll(dao.getDoctorsByName(query));
            doctors.addAll(dao.getDoctorsFromDepartmentByDepartmentName(query));
            doctors = doctors.stream().distinct().collect(Collectors.toList());
        }
        else
        {
            doctors = dao.getAllDoctors();
        }

        places.clear();
        doctors.forEach(doctor -> {
            Department department = dao.getDepartmentByID(doctor.getDepartment_id());

            places.add(new Place(doctor.getAmbulance_name(), department.getName(), department.getFloor(),
                    doctor.getName(), doctor.getStart_time(), doctor.getEnd_time(),
                    doctor.getPhone_number(), doctor.getWeb_site(), doctor.getIsFavorite()));
        });

        placeRecycleAdapter.notifyDataSetChanged();

        if (doctors.size() == 0)
        {
            MessageToast.makeToast(this, R.string.no_results, Toast.LENGTH_SHORT).show();
        }
    }


    private void renderScrollList()
    {
        this.placeRecycleAdapter = new PlaceRecycleAdapter(this.places, this);
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

    @Override
    public void onPlaceClick(int position)
    {
        Log.d(TAG, "onPlaceClick: "+ position);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("selected_place", places.get(position));
        startActivity(intent);
    }

    @Override
    public void onNavigateClick(int position)
    {
        Log.d(TAG, "onNavigateClick: " + position);
        MessageToast.makeToast(this, "navigate", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavouriteClick(int position) {
        Log.d(TAG, "onNavigateClick: " + position);
        Place touchedPlace = places.get(position);
        int favourite;

        if (touchedPlace.isFavourite() == 0)
        {
            MessageToast.makeToast(this, R.string.add_to_fav, Toast.LENGTH_SHORT).show();
            favourite = 1;
        }
        else
        {
            MessageToast.makeToast(this, R.string.rem_from_fav, Toast.LENGTH_SHORT).show();
            favourite = 0;
        }

        List<Doctor> tmp = dao.getDoctorsByName(touchedPlace.getDoctorsName());
        if (Objects.requireNonNull(tmp).size() > 0)
        {
            Doctor doctor = tmp.get(0);
            doctor.setIsFavorite(favourite);
            dao.updatedDoctor(doctor);
        }

        touchedPlace.setFavourite(favourite);
        placeRecycleAdapter.notifyItemChanged(position);
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
                    Log.d(TAG, "already here");
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
    }
}
