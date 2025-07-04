package com.example.schedulemedical.ui.doctorprofile;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;

import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;

public class DoctorProfileActivity extends BaseActivity {

    public static class Certification {
        public String fileUrl;
        public Certification(String fileUrl) { this.fileUrl = fileUrl; }
    }

    private DoctorViewModel viewModel;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_doctor_profile;
    }

    @Override
    protected void setupViews() {

        Toast.makeText(DoctorProfileActivity.this, "DoctorProfileActivity setupViews", Toast.LENGTH_SHORT).show();
        setupNavigation();
        handleIntentExtras();
        setupViewModel();
        loadDoctorProfileByUserId();
        setupCertifications();
    }

    private void setupNavigation() {
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> NavigationHelper.goBack(this));
        }

        Button chatButton = findViewById(R.id.btnChat);
        if (chatButton != null) {
            chatButton.setOnClickListener(view -> NavigationHelper.navigateToChat(this));
        }

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
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId != -1) {
            Toast.makeText(DoctorProfileActivity.this, "Loading doctor ID: " + doctorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
    }

    private void loadDoctorProfileByUserId() {
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId == -1) {
            AuthManager authManager = new AuthManager(this);
            doctorId = authManager.getUserId();
            Toast.makeText(DoctorProfileActivity.this, "Load profile bằng userId (self): " + doctorId, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DoctorProfileActivity.this, "Load profile bằng doctorId: " + doctorId, Toast.LENGTH_SHORT).show();
        }

        viewModel.loadDoctorProfileByUserId(doctorId);

        viewModel.doctorProfile.observe(this, doctor -> {
            if (doctor != null) {
                mapDoctorProfileToUI(doctor);
            } else {
                Toast.makeText(DoctorProfileActivity.this, "Không thể tải thông tin bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapDoctorProfileToUI(DoctorResponse doctor) {
        Log.d("DoctorProfile", "Bắt đầu map UI");

        TextView doctorName = findViewById(R.id.tvDoctorName);
        TextView doctorSpecialty = findViewById(R.id.tvDoctorSpecialty);
        TextView doctorHospital = findViewById(R.id.tvDoctorHospital);
        TextView doctorRating = findViewById(R.id.tvDoctorRating);
        TextView doctorExperience = findViewById(R.id.tvDoctorExperience);
        ImageView doctorAvatar = findViewById(R.id.ivDoctorAvatar);

        try {
            if (doctorName != null && doctor.getUser() != null) {
                doctorName.setText(doctor.getUser().getFullName());
                Log.d("DoctorProfile", "Set tên: " + doctor.getUser().getFullName());
            }

            if (doctorSpecialty != null) {
                if (doctor.getSpecialty() != null) {
                    doctorSpecialty.setText(doctor.getSpecialty().getName());
                    Log.d("DoctorProfile", "Set chuyên khoa: " + doctor.getSpecialty().getName());
                } else {
                    doctorSpecialty.setText("Chưa cập nhật chuyên khoa");
                    Log.d("DoctorProfile", "Không có chuyên khoa");
                }
            }

            if (doctorHospital != null) {
                if (doctor.getHospital() != null) {
                    doctorHospital.setText(doctor.getHospital().getName());
                    Log.d("DoctorProfile", "Set bệnh viện: " + doctor.getHospital().getName());
                } else {
                    doctorHospital.setText("Chưa cập nhật bệnh viện");
                    Log.d("DoctorProfile", "Không có bệnh viện");
                }
            }

            if (doctorRating != null) {
                String rating = doctor.getRating() != null ? String.format("%.1f ★", doctor.getRating()) : "Chưa có đánh giá";
                doctorRating.setText(rating);
                Log.d("DoctorProfile", "Set rating: " + rating);
            }

            if (doctorExperience != null) {
                String exp = doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() : "Chưa cập nhật kinh nghiệm";
                doctorExperience.setText(exp);
                Log.d("DoctorProfile", "Set kinh nghiệm: " + exp);
            }

            if (doctorAvatar != null && doctor.getUser() != null && doctor.getUser().getAvatar() != null) {
                Log.d("DoctorProfile", "Avatar URL: " + doctor.getUser().getAvatar());
                // TODO: Glide or Picasso load avatar here
            }

        } catch (Exception e) {
            Log.e("DoctorProfile", "Lỗi khi map UI: " + e.getMessage(), e);
            Toast.makeText(DoctorProfileActivity.this, "Lỗi khi map UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void setupCertifications() {
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId == -1) return;

        viewModel.loadDoctorCertifications(doctorId, 1, 10);

        LinearLayout layoutCertifications = findViewById(R.id.layoutCertifications);

        viewModel.certifications.observe(this, response -> {
            if (layoutCertifications == null || response == null || response.getData() == null) return;

            layoutCertifications.removeAllViews();

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
