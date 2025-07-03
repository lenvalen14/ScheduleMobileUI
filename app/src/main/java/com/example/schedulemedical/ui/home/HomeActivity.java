package com.example.schedulemedical.ui.home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.repository.HomeRepository;
import com.example.schedulemedical.services.NotificationService;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.login.LoginActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    
    // Repository and managers
    private HomeRepository homeRepository;
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    
    // UI Components
    private TextView tvUsername;
    private TextView tvWelcome;
    private ImageView ivNotification;
    private LinearLayout searchBar;
    private TextView tvDoctorName;
    private TextView tvAppointmentDesc;
    private ImageView ivDoctorAvatar;
    
    // Quick navigation
    private TextView tvHospital;
    private TextView tvSpecialty;
    private TextView tvDoctor;
    
    // Notification receiver
    private BroadcastReceiver notificationReceiver;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setupViews() {
        // Initialize API client and managers
        ApiClient.init(this);
        authManager = new AuthManager(this);
        homeRepository = new HomeRepository(this);
        
        // Check authentication
        if (!authManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }
        
        initializeViews();
        setupClickListeners();
        setupProgressDialog();
        setupNotificationReceiver();
        startNotificationService();
        loadDashboardData();
    }
    
    private void initializeViews() {
        // Header components
        tvUsername = findViewById(R.id.tvUsername);
        tvWelcome = findViewById(R.id.tvWelcome);
        ivNotification = findViewById(R.id.ivNotification);
        
        // Search bar
        searchBar = findViewById(R.id.layoutSearch);
        
        // Appointment card
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvAppointmentDesc = findViewById(R.id.tvAppointmentDesc);
        ivDoctorAvatar = findViewById(R.id.ivDoctorAvatar);
        
        // Quick navigation
        tvHospital = findViewById(R.id.tvHospital);
        tvSpecialty = findViewById(R.id.tvSpecialty);
        tvDoctor = findViewById(R.id.tvDoctor);
        
        // Set initial user info from cache
        String userName = authManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvUsername.setText(userName);
        } else {
            tvUsername.setText("User");
        }
        
        setupBottomNavigation();
    }
    
    private void setupClickListeners() {
        // Quick navigation clicks
        if (tvHospital != null) {
            tvHospital.setOnClickListener(view -> {
                NavigationHelper.navigateToHospital(this);
            });
        }

        if (tvSpecialty != null) {
            tvSpecialty.setOnClickListener(view -> {
                // TODO: Navigate to specialties
                Toast.makeText(this, "Specialties feature coming soon!", Toast.LENGTH_SHORT).show();
            });
        }

        if (tvDoctor != null) {
            tvDoctor.setOnClickListener(view -> {
                NavigationHelper.navigateToFilterDoctor(this);
            });
        }
        
        // Search bar click
        if (searchBar != null) {
            searchBar.setOnClickListener(view -> {
                // TODO: Navigate to search screen
                NavigationHelper.navigateToFilterDoctor(this);
            });
        }
        
        // Notification click
        if (ivNotification != null) {
            ivNotification.setOnClickListener(view -> {
                // TODO: Navigate to notifications
                Toast.makeText(this, "Notifications feature coming soon!", Toast.LENGTH_SHORT).show();
            });
        }
        
        // Appointment card click
        View appointmentCard = findViewById(R.id.layoutAppointmentCard);
        if (appointmentCard != null) {
            appointmentCard.setOnClickListener(view -> {
                // TODO: Navigate to appointment details or appointment list
                Toast.makeText(this, "Navigate to appointments", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);
    }
    
    private void setupNotificationReceiver() {
        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (NotificationService.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                    String type = intent.getStringExtra(NotificationService.EXTRA_NOTIFICATION_TYPE);
                    String title = intent.getStringExtra(NotificationService.EXTRA_NOTIFICATION_TITLE);
                    String message = intent.getStringExtra(NotificationService.EXTRA_NOTIFICATION_MESSAGE);
                    
                    handleNotificationReceived(type, title, message);
                }
            }
        };
        
        // Register receiver
        IntentFilter filter = new IntentFilter(NotificationService.ACTION_NOTIFICATION_RECEIVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver, filter);
    }
    
    private void startNotificationService() {
        if (authManager.isLoggedIn()) {
            NotificationService.startService(this);
            Log.d(TAG, "Notification service started");
        }
    }
    
    private void handleNotificationReceived(String type, String title, String message) {
        Log.d(TAG, "Notification received - Type: " + type + ", Title: " + title + ", Message: " + message);
        
        // Show in-app notification or update UI based on type
        switch (type) {
            case "appointment":
                // Refresh appointment data
                loadDashboardData();
                showInAppNotification("📅 " + title, message);
                break;
            case "payment":
                showInAppNotification("💳 " + title, message);
                break;
            case "message":
                showInAppNotification("💬 " + title, message);
                break;
            default:
                showInAppNotification(title, message);
                break;
        }
    }
    
    private void showInAppNotification(String title, String message) {
        // For now, just show a toast
        // In a real implementation, you might show a snackbar or custom notification view
        Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
    }
    
    private void loadDashboardData() {
        showLoading();
        
        homeRepository.loadDashboardData(new HomeRepository.DashboardCallback() {
            @Override
            public void onDashboardDataLoaded(HomeRepository.DashboardData data) {
                runOnUiThread(() -> {
                    hideLoading();
                    updateUI(data);
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    hideLoading();
                    Log.e(TAG, "Dashboard data load error: " + error);
                    Toast.makeText(HomeActivity.this, "Có lỗi khi tải dữ liệu: " + error, Toast.LENGTH_LONG).show();
                    
                    // Set fallback data
                    setFallbackUserInfo();
                });
            }
        });
    }
    
    private void updateUI(HomeRepository.DashboardData data) {
        try {
            // Update user profile
            if (data.getUserProfile() != null) {
                updateUserProfile(data.getUserProfile());
            } else {
                setFallbackUserInfo();
            }
            
            // Update appointment info
            if (data.getUpcomingAppointments() != null) {
                updateAppointmentCard(data.getUpcomingAppointments());
            } else {
                setFallbackAppointmentInfo();
            }
            
            // TODO: Update other dashboard components when UI is ready
            // updateStats(data.getAppointmentCounts());
            // updateTopDoctors(data.getTopDoctors());
            // updateNearbyHospitals(data.getNearbyHospitals());
            
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
            setFallbackUserInfo();
            setFallbackAppointmentInfo();
        }
    }
    
    private void updateUserProfile(com.example.schedulemedical.model.dto.response.ProfileResponse profile) {
        if (tvUsername != null && profile.getFullName() != null) {
            tvUsername.setText(profile.getFullName());
        }
        
        if (tvWelcome != null) {
            String greeting = getGreeting();
            tvWelcome.setText(greeting);
        }
    }
    
    private void updateAppointmentCard(Object appointments) {
        // For now, show a placeholder since we need to parse the JSON
        if (tvDoctorName != null) {
            tvDoctorName.setText("Dr. John Doe");
        }
        
        if (tvAppointmentDesc != null) {
            tvAppointmentDesc.setText("You have upcoming appointments");
        }
        
        // TODO: Parse appointment data and update card
        Log.d(TAG, "Appointments data: " + appointments.toString());
    }
    
    private void setFallbackUserInfo() {
        String userName = authManager.getUserName();
        if (tvUsername != null) {
            tvUsername.setText(userName != null ? userName : "User");
        }
        
        if (tvWelcome != null) {
            tvWelcome.setText(getGreeting());
        }
    }
    
    private void setFallbackAppointmentInfo() {
        if (tvDoctorName != null) {
            tvDoctorName.setText("Chưa có lịch hẹn");
        }
        
        if (tvAppointmentDesc != null) {
            tvAppointmentDesc.setText("Đặt lịch khám với bác sĩ ngay!");
        }
    }
    
    private String getGreeting() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        
        if (hour < 12) {
            return "Chào buổi sáng,";
        } else if (hour < 18) {
            return "Chào buổi chiều,";
        } else {
            return "Chào buổi tối,";
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            // Set current item as Home
            bottomNav.setSelectedItemId(R.id.nav_home);
            
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    return true; // Already on home
                } else if (itemId == R.id.nav_explore) {
                    NavigationHelper.navigateToHospital(this);
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    // TODO: Navigate to appointments/calendar screen
                    Toast.makeText(this, "Appointments feature coming soon!", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    NavigationHelper.navigateToUserProfile(this);
                    return true;
                }

                return false;
            });
        }
    }
    
    private void showLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
    
    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    
    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to home
        if (authManager.isLoggedIn()) {
            loadDashboardData();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        
        // Unregister notification receiver
        if (notificationReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver);
        }
    }
}
