package com.example.schedulemedical.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.Doctor;
import com.example.schedulemedical.ui.doctorprofile.DoctorViewModel;
import com.example.schedulemedical.ui.home.HomeActivity;
import com.example.schedulemedical.ui.hospital.HospitalActivity;
import com.example.schedulemedical.ui.profile.MainProfileActivity;
import com.example.schedulemedical.ui.schedule.MyScheduledActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        setupBottomNavigation();
        setupViews();
    }

    protected abstract int getLayoutResourceId();
    protected abstract void setupViews();

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigationItemSelected);
            setSelectedNavigationItem();
        }
    }

    private boolean handleBottomNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            if (!(this instanceof HomeActivity)) {
                NavigationHelper.navigateToHome(this);
            }
            return true;
        } else if (itemId == R.id.nav_explore) {
            if (!(this instanceof HospitalActivity)) {
                NavigationHelper.navigateToHospital(this);
            }
            return true;
        } else if (itemId == R.id.nav_calendar) {
            if (!(this instanceof MyScheduledActivity)) {
                AuthManager authManager = new AuthManager(this);
                String role = authManager.getUserRole();
                int userId = authManager.getUserId();

                if ("DOCTOR".equalsIgnoreCase(role)) {
                    DoctorViewModel viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
                    viewModel.loadDoctorProfileByUserId(userId);
                    viewModel.doctorProfile.observe(this, doctor -> {
                        viewModel.doctorProfile.removeObservers(this);
                        if (doctor != null) {
                            Intent intent = new Intent(this, MyScheduledActivity.class);
                            intent.putExtra("role", role);
                            intent.putExtra("doctorId", doctor.getDoctorId());
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Không tìm thấy hồ sơ bác sĩ", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Intent intent = new Intent(this, MyScheduledActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);
                }
            }
            return true;
        } else if (itemId == R.id.nav_profile) {
            if (!(this instanceof MainProfileActivity)) {
                AuthManager authManager = new AuthManager(this);
                String role = authManager.getUserRole();
                NavigationHelper.navigateToMainProfile(this, role);
            }
            return true;
        }

        return false;
    }

    private void setSelectedNavigationItem() {
        if (bottomNavigationView == null) return;

        int selectedItemId = R.id.nav_home;

        if (this instanceof HomeActivity) {
            selectedItemId = R.id.nav_home;
        } else if (this instanceof HospitalActivity) {
            selectedItemId = R.id.nav_explore;
        } else if (this instanceof MyScheduledActivity) {
            selectedItemId = R.id.nav_calendar;
        } else if (this instanceof MainProfileActivity) {
            selectedItemId = R.id.nav_profile;
        }

        bottomNavigationView.setSelectedItemId(selectedItemId);
    }

    protected void setBottomNavigationVisibility(boolean visible) {
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(visible ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }
}
