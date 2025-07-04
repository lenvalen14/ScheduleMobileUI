package com.example.schedulemedical.ui.doctorprofile;

import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;
import com.example.schedulemedical.model.dto.response.DoctorResponse;

import java.util.List;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

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
        Toast.makeText(this, "DoctorProfileActivity setupViews", Toast.LENGTH_SHORT).show();
        setupNavigation();
        handleIntentExtras();
        loadDoctorProfileByUserId();
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

    private void loadDoctorProfileByUserId() {
        AuthManager authManager = new AuthManager(this);
        int userId = authManager.getUserId();
        Toast.makeText(this, "UserId: " + userId, Toast.LENGTH_SHORT).show();
        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
        viewModel.loadDoctorProfileByUserId(userId);
        viewModel.doctorProfile.observe(this, doctor -> {
            if (doctor != null) {
                Toast.makeText(this, "Có dữ liệu doctor", Toast.LENGTH_SHORT).show();
                mapDoctorProfileToUI(doctor);
            } else {
                Toast.makeText(this, "Không lấy được profile bác sĩ!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapDoctorProfileToUI(DoctorResponse doctor) {
        Log.d("DoctorProfile", "doctorId: " + doctor.getDoctorId());
        Log.d("DoctorProfile", "user: " + (doctor.getUser() != null ? doctor.getUser().getFullName() : "null"));
        Log.d("DoctorProfile", "specialty: " + (doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "null"));
        Log.d("DoctorProfile", "hospital: " + (doctor.getHospital() != null ? doctor.getHospital().getName() : "null"));
        Log.d("DoctorProfile", "rating: " + doctor.getRating());
        Log.d("DoctorProfile", "yearsOfExperience: " + doctor.getYearsOfExperience());
        Toast.makeText(this, "Bắt đầu map UI", Toast.LENGTH_SHORT).show();
        TextView doctorName = findViewById(R.id.tvDoctorName);
        TextView doctorSpecialty = findViewById(R.id.tvDoctorSpecialty);
        TextView doctorHospital = findViewById(R.id.tvDoctorHospital);
        TextView doctorRating = findViewById(R.id.tvDoctorRating);
        TextView doctorExperience = findViewById(R.id.tvDoctorExperience);
        ImageView doctorAvatar = findViewById(R.id.ivDoctorAvatar);
        try {
            if (doctorName != null && doctor.getUser() != null) {
                doctorName.setText(doctor.getUser().getFullName());
                Toast.makeText(this, "Set tên xong", Toast.LENGTH_SHORT).show();
            }
            // Specialty
            if (doctorSpecialty != null) {
                if (doctor.getSpecialty() != null && doctor.getSpecialty().getName() != null) {
                    doctorSpecialty.setText(doctor.getSpecialty().getName());
                    Toast.makeText(this, "Set chuyên khoa xong", Toast.LENGTH_SHORT).show();
                } else {
                    doctorSpecialty.setText("Chưa cập nhật chuyên khoa");
                    Toast.makeText(this, "Chuyên khoa null", Toast.LENGTH_SHORT).show();
                }
            }
            // Hospital
            if (doctorHospital != null) {
                if (doctor.getHospital() != null && doctor.getHospital().getName() != null) {
                    doctorHospital.setText(doctor.getHospital().getName());
                    Toast.makeText(this, "Set bệnh viện xong", Toast.LENGTH_SHORT).show();
                } else {
                    doctorHospital.setText("Chưa cập nhật bệnh viện");
                    Toast.makeText(this, "Bệnh viện null", Toast.LENGTH_SHORT).show();
                }
            }
            // Rating
            if (doctorRating != null) {
                String ratingStr = doctor.getRating() != null ? String.format("%.1f", doctor.getRating()) : "Chưa có đánh giá";
                doctorRating.setText(ratingStr);
                Toast.makeText(this, "Set rating xong", Toast.LENGTH_SHORT).show();
            }
            // Experience
            if (doctorExperience != null) {
                String expStr = doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() : "Chưa cập nhật kinh nghiệm";
                doctorExperience.setText(expStr);
                Toast.makeText(this, "Set kinh nghiệm xong", Toast.LENGTH_SHORT).show();
            }
            // Avatar
            if (doctorAvatar != null && doctor.getUser() != null && doctor.getUser().getAvatar() != null) {
                // Glide.with(this).load(doctor.getUser().getAvatar()).into(doctorAvatar);
                Toast.makeText(this, "Set avatar xong", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi map UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
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