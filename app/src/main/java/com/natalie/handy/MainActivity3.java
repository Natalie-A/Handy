package com.natalie.handy;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity3 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout2);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace
                    (R.id.fragment_container2, new RequestsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_requests);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_requests:
                getSupportFragmentManager().beginTransaction().replace
                        (R.id.fragment_container2, new RequestsFragment()).commit();
                break;
            case R.id.nav_profile2:
                getSupportFragmentManager().beginTransaction().replace
                        (R.id.fragment_container2, new ProfileFragment2()).commit();
                break;
            case R.id.nav_history2:
                getSupportFragmentManager().beginTransaction().replace
                        (R.id.fragment_container2, new HistoryFragment2()).commit();
                break;
            case R.id.nav_logout2:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                //when the user clicks back button in login page,it does not go back to profile page
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_profileImage:
                getSupportFragmentManager().beginTransaction().replace
                        (R.id.fragment_container2, new UpdateProfileFragment2()).commit();
                break;
            case R.id.nav_ongoing2:
                getSupportFragmentManager().beginTransaction().replace
                        (R.id.fragment_container2, new OnGoingRequestsFragment2()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}