package com.example.moneymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout frameLayout;

    //Frame
    private DashboardFragment dashboardFragment;
    private ExpenseFragment expenseFragment;
    private IncomeFragment incomeFragment;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = findViewById(R.id.myToolBar);

        firebaseAuth = FirebaseAuth.getInstance();

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        frameLayout = findViewById(R.id.mainFrame);
        dashboardFragment = new DashboardFragment();
        expenseFragment = new ExpenseFragment();
        incomeFragment = new IncomeFragment();

        setFragment(dashboardFragment); //default fragment
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        if(drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        }
        else {
            super.onBackPressed(); //return from one activity to other activity
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        showSelectedListener(item.getItemId());
        return true;
    }

    //show navigation drawer
    public void showSelectedListener(int id) {
        Fragment fragment = null;

        switch (id) {
            case R.id.icExpense:
                fragment = new ExpenseFragment();
                break;
            case R.id.icIncome:
                fragment = new IncomeFragment();
                break;
            case R.id.icHome:
                fragment = new DashboardFragment();
                break;
            case R.id.icSignOut:
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
            case R.id.icStatistic:
                startActivity(new Intent(getApplicationContext(), StatisticActivity.class));
                break;
        }

        if(fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mainFrame, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    //set fragment
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();
    }
}