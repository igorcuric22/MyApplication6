package com.example.myapplication6;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        Button btnFragmentA = findViewById(R.id.btn_fragment_a);
        Button btnFragmentB = findViewById(R.id.btn_fragment_b);

        // Setup drawer toggle initially
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Default fragment on start
        if (savedInstanceState == null) {
            replaceFragment(new FragmentA(), "Fragment A", true);
        }

        btnFragmentA.setOnClickListener(v -> {
            replaceFragment(new FragmentA(), "Fragment A", true);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        btnFragmentB.setOnClickListener(v -> {
            String storedName = LocalStorage.getData(this, "name");

            if (storedName != null && !storedName.isEmpty()) { 
                // If name is stored, go to FragmentC immediately
                replaceFragment(new FragmentC(), "Fragment C", false);
                Toast.makeText(this, "Navigating to Fragment C", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, ask user for name in FragmentB
                replaceFragment(new FragmentB(), "Fragment B", true);
                Toast.makeText(this, "Navigating to Fragment B", Toast.LENGTH_SHORT).show();
                
            }
            drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    public void replaceFragment(Fragment fragment, String title, boolean showHamburger) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null) // Allow back navigation
                .commit();

        // Update toolbar title
        getSupportActionBar().setTitle(title);

        if (fragment instanceof FragmentC) {

            // Hide both hamburger and back button for Fragment C
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            toolbar.setNavigationIcon(null); // Remove any icons from toolbar
        } else if (showHamburger) {
            // Restore hamburger menu
            restoreHamburger();
        } else {
            // Show back button instead of hamburger
            toggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    public void restoreHamburger() {
        // Reattach drawer toggle to ensure hamburger opens the drawer
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.syncState();
    }
}

