package com.example.schedulemedical.ui.doctorprofile;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;

public class DoctorProfileActivity extends BaseActivity {

    // Giả lập model Certification
    public static class Certification {
        public String fileUrl;
        public Certification(String fileUrl) { this.fileUrl = fileUrl; }
    }

    private final MutableLiveData<List<Certification>> certificationsLiveData = new MutableLiveData<>();
    private DoctorViewModel viewModel;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_doctor_profile;
    }

    @Override
    protected void setupViews() {
        setupNavigation();
        handleIntentExtras();
        loadDoctorData();
        setupCertifications();
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

    private void setupCertifications() {
        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId == -1) return;
        viewModel.loadDoctorCertifications(doctorId, 1, 10);
        LinearLayout layoutCertifications = findViewById(R.id.layoutCertifications);
        viewModel.certifications.observe(this, response -> {
            if (layoutCertifications == null) return;
            layoutCertifications.removeAllViews();
            if (response == null || response.getData() == null) return;
            for (CertificationResponseDTO cert : response.getData()) {
                TextView fileView = new TextView(this);
                String fileUrl = cert.getFileUrl();
                fileView.setText(fileUrl.substring(fileUrl.lastIndexOf('/') + 1));
                fileView.setTextColor(getResources().getColor(R.color.md_theme_primary));
                fileView.setPadding(0, 8, 0, 8);
                fileView.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                    startActivity(intent);
                });
                layoutCertifications.addView(fileView);
            }
        });
    }
} 