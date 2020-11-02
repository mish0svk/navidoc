package com.example.navidoc;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageButton searchButton;
    private ConstraintLayout searchLayout;
    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.searchButton = findViewById(R.id.search_button_main);
        this.searchLayout = findViewById(R.id.search_layout);
        this.searchField = findViewById(R.id.search_field);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setCheckedItem(R.id.nav_home);
        navigationView.getBackground().setAlpha(200);

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
        navigationView.setNavigationItemSelectedListener(item -> false);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed()
    {
        if (this.searchLayout.getVisibility() == View.VISIBLE)
        {
            this.searchLayout.setVisibility(View.INVISIBLE);
        }

        super.onBackPressed();
    }

}