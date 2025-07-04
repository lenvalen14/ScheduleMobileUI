package com.example.schedulemedical.ui.userprofile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class UserProfileActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Implement user profile functionality
        // For now, this is just a placeholder to fix compilation errors
        finish();
    }
    
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home; // TODO: Create proper activity_user_profile layout
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        loadUserData();
    }

    private void setupNavigation() {
        // Nút back
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> NavigationHelper.goBack(this));
        }

        // BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    NavigationHelper.navigateToHome(this);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    NavigationHelper.navigateToHome(this);
                    return true;
                } else if (itemId == R.id.nav_explore) {
                    NavigationHelper.navigateToHospital(this);
                    return true;
                }
                return false;
            });

            // (Optional) Chọn mặc định là profile nếu đang ở UserProfileActivity
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }

        // Nút chỉnh sửa thông tin
        Button editProfileButton = findViewById(R.id.btn_save);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(view -> {
                // TODO: Điều hướng sang màn hình chỉnh sửa
                // NavigationHelper.navigateToEditProfile(this);
            });
        }
    }

    private void loadUserData() {
        // TODO: Load dữ liệu người dùng từ API hoặc SharedPreferences
        // TextView userName = findViewById(R.id.et_first_name);
        // TextView userEmail = findViewById(R.id.et_email);
        // TextView address = findViewById(R.id.et_address);

        // TODO: Implement when proper layout exists
        /*
        if (userName != null) {
            userName.setText("John Doe");
        }
        if (userEmail != null) {
            userEmail.setText("john.doe@example.com");
        }
        if (address != null) {
            address.setText("123 Main Street");
        }
        */
    }
}
