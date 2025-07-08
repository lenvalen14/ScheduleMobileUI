package com.example.schedulemedical.ui.home;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.schedulemedical.Adapter.DoctorAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.repository.HomeRepository;
import com.example.schedulemedical.services.NotificationService;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.filterDoctor.FilterDoctorActivity;
import com.example.schedulemedical.ui.login.LoginActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.schedulemedical.data.repository.HospitalRepository;
import com.example.schedulemedical.data.repository.SpecialtyRepository;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import java.util.List;
import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.model.Specialty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.schedulemedical.model.dto.response.HospitalListResponse;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.Adapter.HospitalCardAdapter;
import com.example.schedulemedical.Adapter.SpecialtyCardAdapter;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";

    // Repository and managers
    private AuthManager authManager;
    private ProgressDialog progressDialog;
    private HospitalRepository hospitalRepository;
    private SpecialtyRepository specialtyRepository;
    private DoctorRepository doctorRepository;

    // UI Components
    private TextView tvUsername;
    private TextView tvWelcome;
    private ImageView ivNotification;
    private LinearLayout searchBar;
    private TextView tvDoctorName;
    private TextView tvAppointmentDesc;
    private ImageView ivDoctorAvatar;

    // Quick navigation
    private TextView tvSpecialty;
    private TextView tvHospital;
    private TextView tvDoctor;

    // Notification receiver
    private BroadcastReceiver notificationReceiver;

    private RecyclerView recyclerDoctors, recyclerHospitals, recyclerSpecialties;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setupViews() {
        // Initialize API client and managers
        ApiClient.init(this);
        authManager = new AuthManager(this);
        hospitalRepository = new HospitalRepository();
        specialtyRepository = new SpecialtyRepository();
        doctorRepository = new DoctorRepository();

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
        // searchBar = findViewById(R.id.layoutSearch); // Not in layout

        // Appointment card
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvAppointmentDesc = findViewById(R.id.tvAppointmentDesc);
        ivDoctorAvatar = findViewById(R.id.ivDoctorAvatar);

        // Quick navigation
         tvHospital = findViewById(R.id.tvSeeAllHospital); // Not in layout
        tvSpecialty = findViewById(R.id.tvSpecialty);
         tvDoctor = findViewById(R.id.tvDoctor); // Not in layout

        recyclerDoctors = findViewById(R.id.recycler_doctors);
        recyclerHospitals = findViewById(R.id.recycler_hospitals);
        recyclerSpecialties = findViewById(R.id.recycler_specialties);
        // Set LayoutManager
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerHospitals.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerSpecialties.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

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
        if (tvSpecialty != null) {
            tvSpecialty.setOnClickListener(view -> {
                Log.d(TAG, "tvSpecialty clicked, navigating to SpeciatlyActivity");
                NavigationHelper.navigateToSpecialty(this);
//                Toast.makeText(this, "Specialties feature coming soon!", Toast.LENGTH_SHORT).show();
            });
        }

        if (tvDoctor != null) {
            tvDoctor.setOnClickListener(view -> {
                Log.d(TAG, "tvDoctor clicked, navigating to FilterDoctorActivity");
                NavigationHelper.navigateToFilterDoctor(this);
            });
        } else {
            Log.e(TAG, "tvDoctor is NULL. Check layout file ID.");
        }

        if(tvHospital != null){
            tvHospital.setOnClickListener(view -> {
                Log.d(TAG, "tvHospital clicked navigating to HospitalActivity");
                NavigationHelper.navigateToHospital((this));
            });
        }
        else {
            Log.e(TAG, "tvHospital is NULL. Check layout file ID.");
        }


        // Search bar click
        if (searchBar != null) {
            searchBar.setOnClickListener(view -> {
                // TODO: Navigate to search screen
                NavigationHelper.navigateToFilterDoctor(this);
            });
        }

        // Notification click - Show dropdown menu
        if (ivNotification != null) {
            ivNotification.setOnClickListener(view -> {
                showNotificationDropdown(view);
            });
        }

        // Appointment card click
        // View appointmentCard = findViewById(R.id.layoutAppointmentCard); // Not in layout
        // if (appointmentCard != null) {
        //     appointmentCard.setOnClickListener(view -> {
        //         // TODO: Navigate to appointment details or appointment list
        //         Toast.makeText(this, "Navigate to appointments", Toast.LENGTH_SHORT).show();
        //     });
        // }
    }

    private void showNotificationDropdown(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);

        // Manually add menu items since we can't create menu XML file
        popup.getMenu().add(0, R.id.menu_notifications, 0, "📢 Thông báo");
        popup.getMenu().add(0, R.id.menu_profile, 1, "👤 Hồ sơ");
        popup.getMenu().add(0, R.id.menu_settings, 2, "⚙️ Cài đặt");
        popup.getMenu().add(0, R.id.menu_logout, 3, "🚪 Đăng xuất");

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.menu_notifications) {
                // Navigate to notifications
                Toast.makeText(this, "Thông báo đang được phát triển!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.menu_profile) {
                // Navigate to profile
                NavigationHelper.navigateToUserProfile(this);
                return true;
            } else if (itemId == R.id.menu_settings) {
                // Navigate to settings
                Toast.makeText(this, "Cài đặt đang được phát triển!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.menu_logout) {
                // Logout
                performLogout();
                return true;
            }

            return false;
        });

        popup.show();
    }

    private void performLogout() {
        // Show confirmation dialog first
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> {
                showLoading();
                authManager.logout(new AuthManager.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> {
                            hideLoading();
                            Toast.makeText(HomeActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                            redirectToLogin();
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            hideLoading();
                            Toast.makeText(HomeActivity.this, "Lỗi đăng xuất: " + error, Toast.LENGTH_SHORT).show();
                            // Still redirect to login even if logout API fails
                            redirectToLogin();
                        });
                    }
                });
            })
            .setNegativeButton("Hủy", null)
            .show();
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
        // Load doctors
        doctorRepository.filterDoctorsRaw(null, null, null, 1, 10, new Callback<DoctorListResponse>() {
            @Override
            public void onResponse(Call<DoctorListResponse> call, Response<DoctorListResponse> response) {
                Log.d("HomeActivity", "Doctor API response: " + response);
                if (response.isSuccessful() && response.body() != null && recyclerDoctors != null) {
                    List<DoctorResponse> doctorList = response.body().getData();
                    Log.d("HomeActivity", "Doctor raw list size: " + (doctorList != null ? doctorList.size() : 0));

                    DoctorAdapter doctorAdapter = new DoctorAdapter(HomeActivity.this, doctorList);
                    doctorAdapter.setOnDoctorClickListener(new DoctorAdapter.OnDoctorClickListener() {
                        @Override
                        public void onDoctorClick(DoctorResponse doctor) {
                            if (doctor != null && doctor.getUser() != null) {
                                String doctorName = doctor.getUser().getFullName() != null
                                        ? doctor.getUser().getFullName() : "Unknown Doctor";
                                Log.d(TAG, "Doctor clicked: " + doctorName);

                                if (doctor.getDoctorId() != null) {
                                    NavigationHelper.navigateToDoctorProfile(HomeActivity.this, doctor.getUserId());
                                } else {
                                    Toast.makeText(HomeActivity.this, "Không thể xem thông tin bác sĩ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onBookAppointmentClick(DoctorResponse doctor) {
                            if (doctor != null && doctor.getDoctorId() != null) {
                                String doctorName = doctor.getUser() != null ? doctor.getUser().getFullName() : "Unknown";
                                String specialty = doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "Không rõ";
                                String hospital = doctor.getHospital() != null ? doctor.getHospital().getName() : "Không rõ";

                                NavigationHelper.navigateToSchedule(
                                        HomeActivity.this,
                                        doctor.getDoctorId(),
                                        doctorName,
                                        specialty,
                                        hospital
                                );
                            }
                        }
                    });

                    recyclerDoctors.setAdapter(doctorAdapter);
                } else {
                    Log.d("HomeActivity", "Doctor response null or empty, or recyclerDoctors is null");
                }
            }

            @Override
            public void onFailure(Call<DoctorListResponse> call, Throwable t) {
                Log.e("HomeActivity", "Doctor API failure: " + t.getMessage());
            }
        });

        // Load hospitals
        hospitalRepository.getHospitals(1, 10, new Callback<HospitalListResponse>() {
            @Override
            public void onResponse(Call<HospitalListResponse> call, Response<HospitalListResponse> response) {
                Log.d("HomeActivity", "Hospital API response: " + response);
                if (response.isSuccessful() && response.body() != null && recyclerHospitals != null) {
                    List<HospitalResponse> hospitalList = response.body().getData();
                    Log.d("HomeActivity", "Hospital raw list size: " + (hospitalList != null ? hospitalList.size() : 0));
                    HospitalCardAdapter hospitalAdapter = new HospitalCardAdapter(HomeActivity.this, hospitalList);
                    recyclerHospitals.setAdapter(hospitalAdapter);
                } else {
                    Log.d("HomeActivity", "Hospital response null or empty, or recyclerHospitals is null");
                }
            }
            @Override
            public void onFailure(Call<HospitalListResponse> call, Throwable t) {
                Log.e("HomeActivity", "Hospital API failure: " + t.getMessage());
            }
        });
        // Load specialties
        specialtyRepository.getSpecialties(1, 10, new Callback<ApiResponse<List<SpecialtyResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                Log.d("HomeActivity", "Specialty API response: " + response);
                if (response.isSuccessful() && response.body() != null && recyclerSpecialties != null) {
                    List<SpecialtyResponse> rawList = response.body().getData();
                    Log.d("HomeActivity", "Specialty raw list size: " + (rawList != null ? rawList.size() : 0));
                    List<Specialty> specialties = new java.util.ArrayList<>();
                    if (rawList != null) {
                        for (SpecialtyResponse s : rawList) {
                            specialties.add(new Specialty(s.getSpecialtyId(), s.getName(), s.getDescription(), 0));
                        }
                    }
                    SpecialtyCardAdapter specialtyAdapter = new SpecialtyCardAdapter(HomeActivity.this, specialties);
                    recyclerSpecialties.setAdapter(specialtyAdapter);
                } else {
                    Log.d("HomeActivity", "Specialty response null or empty, or recyclerSpecialties is null");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<SpecialtyResponse>>> call, Throwable t) {
                Log.e("HomeActivity", "Specialty API failure: " + t.getMessage());
            }
        });
        hideLoading();
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
                    NavigationHelper.navigateToBookingWizard(this);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    String role = authManager.getUserRole();
                    if ("DOCTOR".equalsIgnoreCase(role)) {
                        NavigationHelper.navigateToDoctorProfile(this);
                    } else {
                        NavigationHelper.navigateToUserProfile(this);
                    }
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
