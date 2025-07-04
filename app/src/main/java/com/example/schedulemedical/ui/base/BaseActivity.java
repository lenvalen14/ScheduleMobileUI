package com.example.schedulemedical.ui.base;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schedulemedical.R;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Base Activity cho các màn hình chính có bottom navigation
 * Các Activity auth (Login, Register, ForgotPassword) không kế thừa từ class này
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        
        setupBottomNavigation();
        setupViews();
    }

    /**
     * Override để trả về layout resource ID của Activity
     */
    protected abstract int getLayoutResourceId();

    /**
     * Override để setup các view khác của Activity
     */
    protected abstract void setupViews();

    /**
     * Setup bottom navigation menu
     */
    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                return handleBottomNavigationItemSelected(item);
            });
            
            // Set selected item based on current activity
            setSelectedNavigationItem();
        }
    }

    /**
     * Handle bottom navigation item selection
     */
    private boolean handleBottomNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.nav_home) {
            if (!(BaseActivity.this instanceof com.example.schedulemedical.ui.home.HomeActivity)) {
                NavigationHelper.navigateToHome(BaseActivity.this);
            }
            return true;
        } else if (itemId == R.id.nav_explore) {
            if (!(BaseActivity.this instanceof com.example.schedulemedical.ui.hospital.HospitalActivity)) {
                NavigationHelper.navigateToHospital(BaseActivity.this);
            }
            return true;
        } else if (itemId == R.id.nav_calendar) {
            NavigationHelper.navigateToBookingWizard(BaseActivity.this);
            return true;
        } else if (itemId == R.id.nav_profile) {
            if (!(BaseActivity.this instanceof com.example.schedulemedical.ui.userprofile.UserProfileActivity)) {
                NavigationHelper.navigateToUserProfile(BaseActivity.this);
            }
            return true;
        }
        
        return false;
    }

    /**
     * Set selected navigation item based on current activity
     */
    private void setSelectedNavigationItem() {
        if (bottomNavigationView == null) return;
        
        int selectedItemId = R.id.nav_home; // Default to home
        
        if (this instanceof com.example.schedulemedical.ui.home.HomeActivity) {
            selectedItemId = R.id.nav_home;
        } else if (this instanceof com.example.schedulemedical.ui.hospital.HospitalActivity) {
            selectedItemId = R.id.nav_explore;
        } else if (this instanceof com.example.schedulemedical.ui.userprofile.UserProfileActivity) {
            selectedItemId = R.id.nav_profile;
        }
        
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    /**
     * Update bottom navigation visibility
     */
    protected void setBottomNavigationVisibility(boolean visible) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(visible ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }
} 