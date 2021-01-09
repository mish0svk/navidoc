package com.example.navidoc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.navidoc.database.DAO;
import com.example.navidoc.database.DatabaseHelper;
import com.example.navidoc.database.DatabaseInitialInsert;
import com.example.navidoc.database.Doctor;
import com.example.navidoc.activities.PlacesActivity;
import com.example.navidoc.adapters.Place;
import com.example.navidoc.adapters.PlaceSearchAdapter;
import com.example.navidoc.services.BackgroundScanService;
import com.example.navidoc.utils.AbstractDialog;
import com.example.navidoc.utils.BeaconUtility;
import com.example.navidoc.utils.Locator;
import com.example.navidoc.utils.MenuUtils;
import com.example.navidoc.utils.MessageToast;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.kontakt.sdk.android.ble.device.BeaconDevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final int REQUEST_CODE_FOR_PERMISSIONS = 100;
    private ImageButton searchButton;
    private ConstraintLayout searchLayout;
    private AutoCompleteTextView searchField;
    private ImageButton submitSearch;
    private DAO dao;
    private static final String TAG = "MainActivity";
    private Locator locator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.searchButton = findViewById(R.id.search_button_main);
        this.searchLayout = findViewById(R.id.search_layout);
        this.searchField = findViewById(R.id.search_field);
        submitSearch = findViewById(R.id.submit_search);

        ImportDataTOdatabase();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_home);
        this.findAndCloseNavigation();
        setMainSearchButtonListener();
        MenuUtils menuUtils = new MenuUtils(this, R.id.nav_home);
        navigationView.setNavigationItemSelectedListener(menuUtils);
        setSubmitSearchButtonListener();
        searchRelevantData();
        checkPermissions();

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



    private void searchRelevantData()
    {
        List<Place> places = new ArrayList<>();
        PlaceSearchAdapter placeSearchAdapter = new PlaceSearchAdapter(getApplicationContext(), places);
        searchField.setThreshold(1);
        searchField.setAdapter(placeSearchAdapter);
        searchField.setOnItemClickListener((parent, view, position, id) -> createNavigateDialog());
    }

    public void createNavigateDialog()
    {
        String searchInput = searchField.getText().toString();
        BeaconDevice beacon = locator.displayClosestBeacon();
        if (beacon == null || BeaconUtility.getUniqueId(beacon) == null)
        {
            MessageToast.makeToast(this, R.string.unavailable_location, Toast.LENGTH_SHORT).show();
            return;
        }

        List<Doctor> tmp = dao.getDoctorsByName(searchInput);
        Doctor doctor = tmp.get(0);
        String navigateTo = getResources().getString(R.string.navigate_to)+ doctor.getAmbulance_name()
                + "("+ doctor.getName() + ")";

        AbstractDialog dialog = AbstractDialog.getInstance();
        dialog.newBuilderInstance(this).setTitle(R.string.app_name).setMessage(navigateTo)
                .setPositiveButtonForMain(BeaconUtility.getUniqueId(beacon), searchInput, tmp).setNegativeButton(this).getBuilder()
                .setNeutralButton(R.string.go_to_places, this::onClick).create().show();
    }

    private void setSubmitSearchButtonListener()
    {
        submitSearch.setOnClickListener(view -> startPlacesActivityWithQuery());
    }

    private void startPlacesActivityWithQuery()
    {
        String searchInput = searchField.getText().toString();
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("searchInput", searchInput);
        startActivity(intent);
    }

    private void setMainSearchButtonListener()
    {
        this.searchButton.setOnClickListener(view ->
        {
            if (this.searchLayout.getVisibility() == View.VISIBLE && !searchField.getText().toString().isEmpty())
            {
                Snackbar.make(view, R.string.toastik, Snackbar.LENGTH_SHORT).show();
            }
            else if (this.searchLayout.getVisibility() == View.INVISIBLE)
            {
                this.searchLayout.setVisibility(View.VISIBLE);
            }
            else
            {
                this.searchLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        locator.stopService();
        boolean sideActivity = findAndCloseNavigation();

        if (this.searchLayout.getVisibility() == View.VISIBLE)
        {
            this.searchLayout.setVisibility(View.INVISIBLE);
            sideActivity = true;
        }

        if (!sideActivity)
        {
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

    private boolean findAndCloseNavigation()
    {
        DrawerLayout layout = findViewById(R.id.drawer_layout);

        if (layout.isDrawerOpen(GravityCompat.START))
        {
            layout.closeDrawer(GravityCompat.START);

            return true;
        }

        return false;
    }

    @SuppressLint("SdCardPath")
    private void ImportDataTOdatabase()
    {

        DatabaseHelper db2 = DatabaseHelper.getInstance(this);
        dao = db2.dao();
        Log.d(TAG, "ImportDataTOdatabase: ");

        if (getDatabasePath("/data/data/com.example.navidoc/databases/HospitalDatabase").exists())
        {
            return;
        }

        DatabaseInitialInsert.insertDb(dao);
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
            MessageToast.makeToast(this, "Location permissions are mandatory to use BLE features on Android 6.0 or higher", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    private void onClick(DialogInterface dialog1, int which)
    {
        startPlacesActivityWithQuery();
    }
}