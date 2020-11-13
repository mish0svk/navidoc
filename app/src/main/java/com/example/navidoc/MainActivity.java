package com.example.navidoc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.example.navidoc.Databse.DAO;
import com.example.navidoc.Databse.DatabaseHelper;
import com.example.navidoc.Databse.Department;
import com.example.navidoc.Databse.Doctor;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private ImageButton searchButton;
    private ConstraintLayout searchLayout;
    private AutoCompleteTextView searchField;
    private NavigationView navigationView;
    private ImageButton submitSearch;
    private DatabaseHelper db2;
    private DAO dao;
    private static final String TAG = "MainActivity";

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
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_home);
        this.findAndCloseNavigation();
        setMainSearchButtonListener();
        setNavigationListener();
        setSubmitSearchButtonListener();
        searchRelevantData();
    }

    private void searchRelevantData()
    {
        List<Place> places = new ArrayList<>();
        PlaceSearchAdapter placeSearchAdapter = new PlaceSearchAdapter(getApplicationContext(), places);
        searchField.setThreshold(1);
        searchField.setAdapter(placeSearchAdapter);
        searchField.setOnItemClickListener((parent, view, position, id) -> {
           startPlacesActivityWithQuery();
        });
    }

    private void setSubmitSearchButtonListener()
    {
        submitSearch.setOnClickListener(view -> {
           startPlacesActivityWithQuery();
        });
    }

    private void startPlacesActivityWithQuery()
    {
        String searchInput = searchField.getText().toString();
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("searchInput", searchInput);
        startActivity(intent);
    }

    private void setMainSearchButtonListener() {
        this.searchButton.setOnClickListener(view -> {
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


    @SuppressLint("NonConstantResourceId")
    private void setNavigationListener()
    {
        navigationView.setNavigationItemSelectedListener(item -> {
            String TAG = "TAG";
            Log.d(TAG, String.valueOf(item.getItemId()));
            switch (item.getItemId())
            {
                case R.id.nav_home:
                    Log.d(TAG, "already here");
                    break;
                case R.id.nav_places:
                    Log.d(TAG, "places");
                    Intent intent = new Intent(this, PlacesActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_current_location:
                    Log.d(TAG, "current location");
                    break;
                default:
                    Log.d(TAG, "others");
            }

            return false;
        });
    }


    @Override
    public void onBackPressed()
    {
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

    private void ImportDataTOdatabase()
    {

        db2 = DatabaseHelper.getInstance(this);
        dao = db2.dao();
        Log.d(TAG, "ImportDataTOdatabase: ");

        if (getDatabasePath("/data/data/com.example.navidoc/databases/HospitalDatabase").exists())
        {
            return;
        }


        dao.insertDepartment(new Department("Dermatologia", 0));
        dao.insertDepartment(new Department("Interne", 5));
        dao.insertDepartment(new Department("Chirurgia", 1));
        dao.insertDepartment(new Department("Oftalmologia", 3));
        dao.insertDepartment(new Department("Imunologia", 2));
        dao.insertDepartment(new Department("RTG", 0));
        dao.insertDepartment(new Department("Geriatria", 2));
        dao.insertDepartment(new Department("Onkologia", 4));
        dao.insertDepartment(new Department("Pediatria", 1));
        dao.insertDepartment(new Department("Gynekologia", 3));

        //Dermatologia
        dao.insertDoctor(new Doctor("Jan Sivy","Derma+","0908 312 789","Derma+@gmail.com",5,"www.Derma+.com","08:00","12:00",0, dao.getDepartmentIdByName("Dermatologia")));
        dao.insertDoctor(new Doctor("Martin Velky","Derma+","0908 312 789","Derma+@gmail.com",7,"www.Derma+.com","08:00","12:00",0, dao.getDepartmentIdByName("Dermatologia")));
        dao.insertDoctor(new Doctor("Peter Novak","DermatologiaMax","0908 123 789","DermaMAX@gmail.com",6,"www.DermaMAX.com","08:00","14:00",0, dao.getDepartmentIdByName("Dermatologia")));

        //Interne
        dao.insertDoctor(new Doctor("Vladislav Kovac","InterLogic","0945 159 789","InterLogic+@gmail.com",53,"www.InterLogic.com","08:00","12:00",0, dao.getDepartmentIdByName("Interne")));
        dao.insertDoctor(new Doctor("Norbert Bajza","Interna ambulacia","0900 315 789","Bajza@gmail.com",54,"www.Bajza+.com","08:00","14:00",0, dao.getDepartmentIdByName("Interne")));
        dao.insertDoctor(new Doctor("Alexander Chrom","Interna ambulacia","0908 432 878","Alexander@gmail.com",55,"www.Alexander.com","07:00","12:00",0, dao.getDepartmentIdByName("Interne")));

        //Chirurgia
        dao.insertDoctor(new Doctor("Dezider Slovak","BrokenArm","0908 312 000","BrokenArm@gmail.com",15,"www.BrokenArm+.com","06:00","12:00",0, dao.getDepartmentIdByName("Chirurgia")));
        dao.insertDoctor(new Doctor("Oliver Stein","Chirurgicka ambulancia","0917 332 789","Stein@gmail.com",16,"www.Stein.com","08:00","14:00",0, dao.getDepartmentIdByName("Chirurgia")));
        dao.insertDoctor(new Doctor("Lenka Sobotova","BrokenArm","0908 312 000","BrokenArm@gmail.com",17,"www.BrokenArm+.com","07:00","13:00",0, dao.getDepartmentIdByName("Chirurgia")));

        //Oftalmologia
        dao.insertDoctor(new Doctor("Adam Klein","Oko+","0908 456 456","Oko+@gmail.com",35,"www.Oko+.com","08:00","14:00",0, dao.getDepartmentIdByName("Oftalmologia")));
        dao.insertDoctor(new Doctor("Anna Jabconova","SuperOko","0908 300 500","SuperOko+@gmail.com",36,"www.SuperOko+.com","08:00","12:00",0, dao.getDepartmentIdByName("Oftalmologia")));
        dao.insertDoctor(new Doctor("Samuel Binas","Ocna Ambulancia","0908 342 342","Binas@gmail.com",37,"www.Binas+.com","08:00","14:00",0, dao.getDepartmentIdByName("Oftalmologia")));

        //Imunologia
        dao.insertDoctor(new Doctor("Jaroslav Nagy","Imuna+","0908 500 200","Imuna+@gmail.com",22,"www.Imuna+.com","06:00","14:00",0, dao.getDepartmentIdByName("Imunologia")));
        dao.insertDoctor(new Doctor("Peter Janus","Imunologicka Ambulancia+","0908 312 444","Janus+@gmail.com",24,"www.Janus+.com","08:00","12:00",0, dao.getDepartmentIdByName("Imunologia")));
        dao.insertDoctor(new Doctor("Dalibor Jenda","Imunologicka Ambulancia+","0908 3399 456","Jenda+@gmail.com",28,"www.Jenda+.com","08:00","12:00",0, dao.getDepartmentIdByName("Imunologia")));

        //RTG
        dao.insertDoctor(new Doctor("Monika Krafova","RongenMaster","0908 987789","RongenMaster+@gmail.com",9,"www.RongenMaster+.com","06:00","12:00",0, dao.getDepartmentIdByName("RTG")));
        dao.insertDoctor(new Doctor("Anton Href","RongenMaster+","0908 987 789","RongenMaster+@gmail.com",8,"www.RongenMaster+.com","08:00","14:00",0, dao.getDepartmentIdByName("RTG")));

        //Geriatria
        dao.insertDoctor(new Doctor("Kristian Onderko","Geriatricka Ambulancia","0908 312 789","GeriatrickaAmbulancia+@gmail.com",21,"www.GeriatrickaAmbulancia++.com","08:00","14:00",0, dao.getDepartmentIdByName("Geriatria")));
        dao.insertDoctor(new Doctor("Klaudia Chroma","Geriatricka Ambulancia+","0908 312 789","GeriatrickaAmbulancia+@gmail.com",23,"www.Geriatricka mbulancia++.com","09:00","16:00",0, dao.getDepartmentIdByName("Geriatria")));

        //Onkologia
        dao.insertDoctor(new Doctor("Vladimir Kralovic","Onkologia","0947 372 789","Onkologia+@gmail.com",42,"www.Onkologia+.com","08:00","13:00",0, dao.getDepartmentIdByName("Onkologia")));
        dao.insertDoctor(new Doctor("Slavka Klimberova","Onkologia","0904 025 789","Onkologia+@gmail.com",43,"www.Onkologia+.com","10:00","17:00",0, dao.getDepartmentIdByName("Onkologia")));

        //Pediatria
        dao.insertDoctor(new Doctor("Peter Mak","DetskaRadost","0908 312 789","DetskaRadost@gmail.com",12,"www.DetskaRadost.com","08:00","14:00",0, dao.getDepartmentIdByName("Pediatria")));
        dao.insertDoctor(new Doctor("Jan Soros","MojeDieta","0908 312 789","MojeDieta@gmail.com",13,"www.MojeDieta.com","08:00","14:00",0, dao.getDepartmentIdByName("Pediatria")));
        dao.insertDoctor(new Doctor("Pavol Richter","DetskaRadost+","0908 312 789","DetskaRadost@gmail.com",14,"www.DetskaRadost.com","08:00","14:00",0, dao.getDepartmentIdByName("Pediatria")));

        //Gynekologia
        dao.insertDoctor(new Doctor("Robert Kocner","Gynekologicka Ambulancia","0908 312 789","Kocner@gmail.com",32,"www.Kocner.com","05:00","12:00",0, dao.getDepartmentIdByName("Gynekologia")));
        dao.insertDoctor(new Doctor("Iveta Rovna","Gynekologicka Ambulancia","0908 312 789","Rovna@gmail.com",33,"www.Rovna.com","06:00","13:00",0, dao.getDepartmentIdByName("Gynekologia")));
        dao.insertDoctor(new Doctor("Julius Bentner","Gynekologicka Ambulancia","0908 312 789","Bentner@gmail.com",34,"www.Bentner.com","07:00","14:00",0, dao.getDepartmentIdByName("Gynekologia")));



    }
}