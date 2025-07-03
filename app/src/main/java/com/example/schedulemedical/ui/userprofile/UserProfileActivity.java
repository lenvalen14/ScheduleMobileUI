package com.example.schedulemedical.ui.userprofile;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class UserProfileActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_user_profile;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        loadUserData();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }

        // Home button
        ImageButton homeButton = findViewById(R.id.nav_home);
        if (homeButton != null) {
            homeButton.setOnClickListener(view -> {
                NavigationHelper.navigateToHome(this);
            });
        }

        // Edit profile button
        Button editProfileButton = findViewById(R.id.btn_save);
        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(view -> {
                // TODO: Navigate to edit profile screen
                // NavigationHelper.navigateToEditProfile(this);
            });
        }
    }

    private void loadUserData() {
        // TODO: Load user data from SharedPreferences or API
        TextView userName = findViewById(R.id.et_first_name);
        TextView userEmail = findViewById(R.id.et_email);
        TextView address = findViewById(R.id.et_address);

        if (userName != null) {
            userName.setText("John Doe"); // Placeholder
        }
        if (userEmail != null) {
            userEmail.setText("john.doe@example.com"); // Placeholder
        }
    }
}