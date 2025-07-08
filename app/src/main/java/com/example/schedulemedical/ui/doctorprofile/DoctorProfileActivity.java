package com.example.schedulemedical.ui.doctorprofile;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.gson.Gson;

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
                DoctorResponse doctor = viewModel.doctorProfile.getValue();
                if (doctor != null && doctor.getDoctorId() != null) {
                    String doctorName = doctor.getUser() != null ? doctor.getUser().getFullName() : "Unknown";
                    String specialty = doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "Chưa rõ";
                    String hospital = doctor.getHospital() != null ? doctor.getHospital().getName() : "Chưa rõ";

                    NavigationHelper.navigateToSchedule(
                            this,
                            doctor.getDoctorId(),
                            doctorName,
                            specialty,
                            hospital
                    );
                } else {
                    Log.e("DoctorProfile", "Không có thông tin bác sĩ để đặt lịch");
                }
            });
        }

    }

    private void handleIntentExtras() {
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId != -1) {
            Log.d("DoctorProfile", "Loading doctor ID: " + doctorId);
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
            Log.d("DoctorProfile", "Load profile bằng userId (self): " + doctorId);
        } else {
            Log.d("DoctorProfile", "Load profile bằng doctorId: " + doctorId);
        }

        viewModel.loadDoctorProfileByUserId(doctorId);

        viewModel.doctorProfile.observe(this, doctor -> {
            if (doctor != null) {
                mapDoctorProfileToUI(doctor);
            } else {
                Log.e("DoctorProfile", "Không thể tải thông tin bác sĩ");
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
        TextView doctorSchedule = findViewById(R.id.tvDoctorSchedule);

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

            if (doctorSchedule != null && doctor.getSchedules() != null && !doctor.getSchedules().isEmpty()) {
                StringBuilder scheduleBuilder = new StringBuilder();

                Log.d("DoctorProfile lich", "Schedules: " + new Gson().toJson(doctor.getSchedules()));

                String[] weekdays = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};

                doctor.getSchedules().stream()
                        .sorted((a, b) -> Integer.compare(a.getDayOfWeek(), b.getDayOfWeek()))
                        .forEach(schedule -> {
                            int dayIndex = schedule.getDayOfWeek();
                            String dayName = (dayIndex >= 1 && dayIndex <= 7) ? weekdays[dayIndex - 1] : "Không rõ";
                            scheduleBuilder.append(dayName)
                                    .append(": ")
                                    .append(schedule.getStartTime())
                                    .append(" - ")
                                    .append(schedule.getEndTime())
                                    .append("\n");
                        });

                doctorSchedule.setText(scheduleBuilder.toString().trim());
                Log.d("DoctorProfile", "Set lịch làm việc:\n" + scheduleBuilder);
            } else if (doctorSchedule != null) {
                doctorSchedule.setText("Chưa cập nhật lịch làm việc");
                Log.d("DoctorProfile lich", "Không có lịch làm việc");
            }

        } catch (Exception e) {
            Log.e("DoctorProfile", "Lỗi khi map UI: " + e.getMessage(), e);
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
                String fileUrl = cert.getFileUrl();

                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 16, 0, 16);
                imageView.setLayoutParams(params);
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                Glide.with(this)
                        .load(fileUrl)
                        .into(imageView);

                layoutCertifications.addView(imageView);
            }
        });
    }
}
