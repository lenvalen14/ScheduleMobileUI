package com.example.schedulemedical.ui.schedule;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class ScheduleActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_schedule;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        handleIntentExtras();
        setupScheduleActions();
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
    }

    private void handleIntentExtras() {
        // Xử lý doctor ID nếu có
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId != -1) {
            // TODO: Load doctor data and schedule based on ID
            Toast.makeText(this, "Loading doctor ID: " + doctorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupScheduleActions() {
        // Confirm schedule button
        Button confirmButton = findViewById(R.id.btnBookAppointment);
        if (confirmButton != null) {
            confirmButton.setOnClickListener(view -> {
                // TODO: Implement schedule confirmation logic
                Toast.makeText(this, "Appointment scheduled successfully!", Toast.LENGTH_SHORT).show();
                NavigationHelper.navigateToMyScheduled(this);
            });
        }

        // Cancel button
        Button cancelButton = findViewById(R.id.btnCancel);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }
    }
}
