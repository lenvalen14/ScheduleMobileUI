package com.example.schedulemedical.ui.filterDoctor;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class FilterDoctorActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_filter_doctor;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        setupFilterControls();
        handleIntentExtras();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }
    }

    private void setupFilterControls() {
        // Apply filter button
        Button applyFilterButton = findViewById(R.id.btnApplyFilter);
        if (applyFilterButton != null) {
            applyFilterButton.setOnClickListener(view -> {
                applyFilters();
            });
        }

        // Clear filter button
        Button clearFilterButton = findViewById(R.id.btnClearFilter);
        if (clearFilterButton != null) {
            clearFilterButton.setOnClickListener(view -> {
                clearFilters();
            });
        }
    }

    private void handleIntentExtras() {
        // Xử lý filter data nếu có
        Bundle filterData = getIntent().getBundleExtra(NavigationHelper.EXTRA_FILTER_DATA);
        if (filterData != null) {
            // TODO: Apply existing filter data
        }
    }

    private void applyFilters() {
        // TODO: Get filter values from UI controls
        Spinner specialtySpinner = findViewById(R.id.spinnerSpecialty);
        Spinner hospitalSpinner = findViewById(R.id.spinnerHospital);
        
        // TODO: Apply filters and update doctor list
        RecyclerView doctorRecyclerView = findViewById(R.id.rvDoctors);
        if (doctorRecyclerView != null) {
            // TODO: Update adapter with filtered results
        }
    }

    private void clearFilters() {
        // TODO: Clear all filter controls
        Spinner specialtySpinner = findViewById(R.id.spinnerSpecialty);
        Spinner hospitalSpinner = findViewById(R.id.spinnerHospital);
        
        if (specialtySpinner != null) {
            specialtySpinner.setSelection(0);
        }
        if (hospitalSpinner != null) {
            hospitalSpinner.setSelection(0);
        }
        
        // TODO: Reload all doctors
        applyFilters();
    }
}