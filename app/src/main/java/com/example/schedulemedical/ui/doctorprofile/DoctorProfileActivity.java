package com.example.schedulemedical.ui.doctorprofile;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

public class DoctorProfileActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_doctor_profile;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        handleIntentExtras();
        loadDoctorData();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }

        // Chat button
        Button chatButton = findViewById(R.id.btnChat);
        if (chatButton != null) {
            chatButton.setOnClickListener(view -> {
                NavigationHelper.navigateToChat(this);
            });
        }

        // Schedule appointment button
        Button scheduleButton = findViewById(R.id.btnSchedule);
        if (scheduleButton != null) {
            scheduleButton.setOnClickListener(view -> {
                int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
                if (doctorId != -1) {
                    NavigationHelper.navigateToSchedule(this, doctorId);
                } else {
                    NavigationHelper.navigateToSchedule(this);
                }
            });
        }
    }

    private void handleIntentExtras() {
        // Xử lý doctor ID
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId != -1) {
            // TODO: Load doctor data based on ID
            Toast.makeText(this, "Loading doctor ID: " + doctorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDoctorData() {
        // TODO: Load doctor data from API
        TextView doctorName = findViewById(R.id.tvDoctorName);
        TextView doctorSpecialty = findViewById(R.id.tvDoctorSpecialty);
        TextView doctorHospital = findViewById(R.id.tvDoctorHospital);
        TextView doctorRating = findViewById(R.id.tvDoctorRating);
        TextView doctorExperience = findViewById(R.id.tvDoctorExperience);
        
        if (doctorName != null) {
            doctorName.setText("Dr. John Smith"); // Placeholder
        }
        if (doctorSpecialty != null) {
            doctorSpecialty.setText("Cardiology"); // Placeholder
        }
        if (doctorHospital != null) {
            doctorHospital.setText("City General Hospital"); // Placeholder
        }
        if (doctorRating != null) {
            doctorRating.setText("4.8 ★"); // Placeholder
        }
        if (doctorExperience != null) {
            doctorExperience.setText("15 years experience"); // Placeholder
        }
    }
} 