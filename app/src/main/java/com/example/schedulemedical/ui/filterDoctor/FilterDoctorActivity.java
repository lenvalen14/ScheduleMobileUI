package com.example.schedulemedical.ui.filterDoctor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.FilterDoctorAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class FilterDoctorActivity extends BaseActivity {

    private static final String TAG = "FilterDoctorActivity";
    private FilterDoctorViewModel viewModel;
    private FilterDoctorAdapter doctorAdapter;
    private RecyclerView recyclerView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_filter_doctor;
    }

    @Override
    protected void setupViews() {
        Log.d(TAG, "FilterDoctorActivity started - setupViews()");

        // Setup UI components
        setupNavigation();
        setupRecyclerView();
        setupBottomNavigation();
        
        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(FilterDoctorViewModel.class);
        observeViewModel();
        
        // Load data after everything is setup
        handleIntentExtras();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_doctors);
        
        if (recyclerView == null) {
            Log.e(TAG, "RecyclerView not found! Check layout ID.");
            return;
        }
        
        // Set layout manager first
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        
        // Create adapter
        doctorAdapter = new FilterDoctorAdapter(this);
        
        // Set adapter after layout manager
        recyclerView.setAdapter(doctorAdapter);
        
        Log.d(TAG, "RecyclerView setup complete - adapter attached");

        doctorAdapter.setOnDoctorClickListener(new FilterDoctorAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(DoctorResponse doctor) {
                if (doctor != null && doctor.getUser() != null) {
                    String doctorName = doctor.getUser().getFullName() != null 
                            ? doctor.getUser().getFullName() : "Unknown Doctor";
                    Log.d(TAG, "Doctor clicked: " + doctorName);
                    
                    // Navigate to doctor profile
                    if (doctor.getDoctorId() != null) {
                        NavigationHelper.navigateToDoctorProfile(FilterDoctorActivity.this, doctor.getDoctorId());
                    } else {
                        Toast.makeText(FilterDoctorActivity.this, "Không thể xem thông tin bác sĩ", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onBookAppointmentClick(DoctorResponse doctor) {
                if (doctor != null && doctor.getUser() != null) {
                    String doctorName = doctor.getUser().getFullName() != null 
                            ? doctor.getUser().getFullName() : "Unknown Doctor";
                    Log.d(TAG, "Book appointment clicked for: " + doctorName);
                    
                    // Navigate to booking/schedule
                    if (doctor.getDoctorId() != null) {
                        NavigationHelper.navigateToSchedule(FilterDoctorActivity.this, doctor.getDoctorId());
                    } else {
                        Toast.makeText(FilterDoctorActivity.this, "Không thể đặt lịch với bác sĩ này", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void setupNavigation() {
        ImageView backButton = findViewById(R.id.btnBack);
        ImageView notificationButton = findViewById(R.id.ivNotification);
        
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                Log.d(TAG, "Back button clicked");
                NavigationHelper.goBack(this);
            });
        }

        if (notificationButton != null) {
            notificationButton.setOnClickListener(view -> {
                Toast.makeText(this, "Notifications feature coming soon!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        if (bottomNavigation != null) {
            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    NavigationHelper.navigateToHome(this);
                    return true;
                } else if (itemId == R.id.nav_explore) {
                    NavigationHelper.navigateToHospital(this);
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    NavigationHelper.navigateToBookingWizard(this);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    NavigationHelper.navigateToUserProfile(this);
                    return true;
                }
                return false;
            });
        }
    }

    private void observeViewModel() {
        viewModel.filteredDoctors.observe(this, response -> {
            Log.d(TAG, "ViewModel response received: " + (response != null ? "success" : "null"));
            
            if (response != null) {
                Log.d(TAG, "Response message: " + response.getMessage());
                Log.d(TAG, "Response data is null: " + (response.getData() == null));
                
                if (response.getData() != null) {
                    Log.d(TAG, "Updating adapter with " + response.getData().size() + " doctors");
                    
                    // Debug first doctor if available
                    if (!response.getData().isEmpty()) {
                        DoctorResponse firstDoctor = response.getData().get(0);
                        Log.d(TAG, "First doctor ID: " + firstDoctor.getDoctorId());
                        if (firstDoctor.getUser() != null) {
                            Log.d(TAG, "First doctor name: " + firstDoctor.getUser().getFullName());
                        }
                    }
                    
                    hideLoadingState();
                    if (doctorAdapter != null) {
                        doctorAdapter.updateDoctors(response.getData());
                    } else {
                        Log.e(TAG, "Adapter is null, cannot update doctors");
                    }
                } else {
                    Log.e(TAG, "Response data is null");
                    hideLoadingState();
                    Toast.makeText(this, "Không có dữ liệu bác sĩ", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Response is null");
                hideLoadingState();
                Toast.makeText(this, "Không thể tải danh sách bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleIntentExtras() {
        Bundle filterData = getIntent().getBundleExtra(NavigationHelper.EXTRA_FILTER_DATA);
        if (filterData != null && !filterData.isEmpty()) {
            Log.d(TAG, "Filter data found, applying filters");
            Integer specialtyId = filterData.getInt("specialtyId", -1);
            Float minRating = filterData.getFloat("minRating", 0f);
            Integer hospitalId = filterData.getInt("hospitalId", -1);

            // Check if any actual filter values are present
            boolean hasFilters = (specialtyId != -1) || (minRating > 0f) || (hospitalId != -1);
            
            if (hasFilters) {
                showLoadingState();
                viewModel.filterDoctors(
                        specialtyId != -1 ? specialtyId : null,
                        minRating > 0f ? minRating : null,
                        hospitalId != -1 ? hospitalId : null,
                        1,
                        20
                );
            } else {
                Log.d(TAG, "Filter data present but empty, loading all doctors");
                loadAllDoctors();
            }
        } else {
            Log.d(TAG, "No filter data, loading all doctors");
            loadAllDoctors();
        }
    }

    private void loadAllDoctors() {
        Log.d(TAG, "Loading all doctors...");
        showLoadingState();
        viewModel.filterDoctors(null, null, null, 1, 50);
    }

    private void showLoadingState() {
        Log.d(TAG, "Showing loading state");
        // You can add a ProgressBar or loading indicator here
    }

    private void hideLoadingState() {
        Log.d(TAG, "Hiding loading state");
        // Hide any loading indicators here
    }
}
