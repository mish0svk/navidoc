package com.example.navidoc.activities;

import android.bluetooth.BluetoothAdapter;
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

import com.example.navidoc.database.DAO;
import com.example.navidoc.database.DatabaseHelper;
import com.example.navidoc.database.Department;
import com.example.navidoc.database.Doctor;
import com.example.navidoc.services.BackgroundScanService;
import com.example.navidoc.utils.AbstractDialog;
import com.example.navidoc.utils.BeaconUtility;
import com.example.navidoc.utils.Locator;
import com.example.navidoc.utils.MenuUtils;
import com.example.navidoc.utils.MessageToast;
import com.example.navidoc.adapters.OnPlaceListener;
import com.example.navidoc.adapters.Place;
import com.example.navidoc.adapters.PlaceRecycleAdapter;
import com.example.navidoc.adapters.PlaceSearchAdapter;
import com.example.navidoc.R;
import com.google.android.material.navigation.NavigationView;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PlacesActivity extends AppCompatActivity implements OnPlaceListener
{
    private final List<Place> places = new ArrayList<>();
    private RecyclerView listView;
    private static final String TAG = "PlacesActivity";
    private PlaceRecycleAdapter placeRecycleAdapter;
    private AutoCompleteTextView searchField;
    private DAO dao;
    private ImageButton submitSearch;
    private Locator locator;

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
        searchField = findViewById(R.id.search_field);
        submitSearch = findViewById(R.id.submit_search);

        //noinspection ConstantConditions
        if (getIntent().hasExtra("searchInput") && getIntent().getStringExtra("searchInput") != null
                && !getIntent().getStringExtra("searchInput").isEmpty())
        {
            searchField.setText(getIntent().getStringExtra("searchInput"));
        }
        else
        {
            //select contetnt when no content is set
            searchField.setText(" ");
        }

        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        setSubmitSearchListener();
        MenuUtils menuUtils = new MenuUtils(this, R.id.nav_places);
        navigationView.setNavigationItemSelectedListener(menuUtils);

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        dao = db.dao();

        searchRelevantData();
        renderScrollList();

        if (!searchField.getText().toString().isEmpty())
        {
            readDataFromDb(searchField.getText().toString());
        }

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

    private void setSubmitSearchListener()
    {
        submitSearch.setOnClickListener(view -> readDataFromDb(searchField.getText().toString()));
    }

    private void searchRelevantData()
    {
        List<Place> places = new ArrayList<>();
        PlaceSearchAdapter placeSearchAdapter = new PlaceSearchAdapter(getApplicationContext(), places);
        searchField.setThreshold(1);
        searchField.setAdapter(placeSearchAdapter);
        searchField.setOnItemClickListener((parent, view, position, id) -> readDataFromDb(searchField.getText().toString()));
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
        locator.stopService();
        DrawerLayout layout = findViewById(R.id.drawer_layout);
        if (layout.isDrawerOpen(GravityCompat.START)) {
            layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @Override
    public void onPlaceClick(int position)
    {
        Log.d(TAG, "onPlaceClick: "+ position);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("selected_place", places.get(position));
        intent.putExtra("activity", "Places");
        intent.putExtra("query", searchField.getText().toString());
        startActivity(intent);
    }

    @Override
    public void onNavigateClick(int position)
    {
        Log.d(TAG, "onNavigateClick: " + position);
        this.createNavigateDialog(position);
    }

    @Override
    public void onFavouriteClick(int position)
    {
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

    public void createNavigateDialog(int position)
    {
        BeaconDevice beacon = locator.displayClosestBeacon();
        if (beacon == null || BeaconUtility.getUniqueId(beacon) == null)
        {
            MessageToast.makeToast(this, R.string.unavailable_location, Toast.LENGTH_SHORT).show();
            return;
        }

        Place touchedPlace = places.get(position);
        String navigateTo = getResources().getString(R.string.navigate_to) + touchedPlace.getAmbulance()
                +"("+ touchedPlace.getDoctorsName() + ")";

        AbstractDialog dialog = AbstractDialog.getInstance();
        dialog.newBuilderInstance(this).setTitle(R.string.app_name).setMessage(navigateTo)
                .sePositiveButton(BeaconUtility.getUniqueId(beacon), touchedPlace).setNegativeButton(this).getBuilder().create().show();
    }
}
