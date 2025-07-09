package com.example.schedulemedical.ui.doctorprofile;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.booking.BookingWizardActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;
import com.google.gson.Gson;

public class DoctorProfileActivity extends BaseActivity {

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
        scheduleButton.setOnClickListener(view -> {
            DoctorResponse doctor = viewModel.doctorProfile.getValue();
            if (doctor != null && doctor.getDoctorId() != null) {
                String doctorName = doctor.getUser() != null ? doctor.getUser().getFullName() : "Unknown";
                String specialty = doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "Chưa rõ";
                String hospital = doctor.getHospital() != null ? doctor.getHospital().getName() : "Chưa rõ";

                if (doctor.getDoctorId() != null) {
                    Intent intent = BookingWizardActivity.createIntentWithDoctor(
                            DoctorProfileActivity.this,
                            doctor
                    );
                    startActivity(intent);
                } else {
                    Toast.makeText(DoctorProfileActivity.this, "Không thể đặt lịch với bác sĩ này", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleIntentExtras() {
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        Log.d("DoctorProfile", "handleIntentExtras - doctorId: " + doctorId);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
    }

    private void loadDoctorProfileByUserId() {
        int doctorId = getIntent().getIntExtra(NavigationHelper.EXTRA_DOCTOR_ID, -1);
        if (doctorId == -1) {
            AuthManager authManager = new AuthManager(this);
            doctorId = authManager.getUserId();
            Log.d("DoctorProfile", "Load profile by userId: " + doctorId);
        } else {
            Log.d("DoctorProfile", "Load profile by doctorId: " + doctorId);
        }

        int finalDoctorId = doctorId;
        viewModel.loadDoctorProfileByUserId(finalDoctorId);

        viewModel.doctorProfile.observe(this, doctor -> {
            Log.d("DoctorProfile", "Doctor profile updated: " + new Gson().toJson(doctor));
            if (doctor != null) {
                mapDoctorProfileToUI(doctor);
//                setupCertifications(doctor.getDoctorId());
            } else {
                Log.e("DoctorProfile", "Doctor profile is null");
            }
        });
    }

    private void mapDoctorProfileToUI(DoctorResponse doctor) {
        TextView doctorName = findViewById(R.id.tvDoctorName);
        TextView doctorSpecialty = findViewById(R.id.tvDoctorSpecialty);
        TextView doctorHospital = findViewById(R.id.tvDoctorHospital);
        TextView doctorRating = findViewById(R.id.tvDoctorRating);
        TextView doctorExperience = findViewById(R.id.tvDoctorExperience);
        ImageView doctorAvatar = findViewById(R.id.ivDoctorAvatar);
        GridLayout gridLayout = findViewById(R.id.gridSchedule);

        if (doctorName != null && doctor.getUser() != null) {
            doctorName.setText(doctor.getUser().getFullName());
        }
        if (doctorSpecialty != null) {
            doctorSpecialty.setText(doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : "Chưa cập nhật chuyên khoa");
        }
        if (doctorHospital != null) {
            doctorHospital.setText(doctor.getHospital() != null ? doctor.getHospital().getName() : "Chưa cập nhật bệnh viện");
        }
        if (doctorRating != null) {
            String rating = doctor.getRating() != null ? String.format("%.1f ★", doctor.getRating()) : "Chưa có đánh giá";
            doctorRating.setText(rating);
        }
        if (doctorExperience != null) {
            String introduction = generateDoctorIntroduction(doctor);
            doctorExperience.setText(introduction);
        }
        if (doctorAvatar != null && doctor.getUser() != null && doctor.getUser().getAvatar() != null) {
            Glide.with(this)
                    .load(doctor.getUser().getAvatar())
                    .placeholder(R.drawable.sample_profile_image)
                    .error(R.drawable.sample_profile_image)
                    .into(doctorAvatar);
        }

        if (gridLayout != null) {
            gridLayout.removeAllViews();
            if (doctor.getSchedules() != null && !doctor.getSchedules().isEmpty()) {
                Log.d("DoctorProfile", "Mapping schedule to UI...");
                LayoutInflater inflater = LayoutInflater.from(this);
                String[] weekdays = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};

                for (var schedule : doctor.getSchedules()) {
                    View itemView = inflater.inflate(R.layout.item_schedule_day, gridLayout, false);
                    TextView tvDayName = itemView.findViewById(R.id.tvDayName);
                    TextView tvDayTime = itemView.findViewById(R.id.tvDayTime);

                    int dayIndex = schedule.getDayOfWeek();
                    String dayName = (dayIndex >= 1 && dayIndex <= 7) ? weekdays[dayIndex - 1] : "Không rõ";

                    tvDayName.setText(dayName);
                    tvDayTime.setText(schedule.getStartTime() + " - " + schedule.getEndTime());

                    gridLayout.addView(itemView);
                    Log.d("DoctorProfile", "Added schedule item: " + dayName);
                }
            } else {
                Log.d("DoctorProfile", "No schedule to display");
                TextView noSchedule = new TextView(this);
                noSchedule.setText("Chưa cập nhật lịch làm việc");
                noSchedule.setTextColor(getColor(R.color.text_secondary));
                gridLayout.addView(noSchedule);
            }
        }
    }


    private String generateDoctorIntroduction(DoctorResponse doctor) {
        StringBuilder intro = new StringBuilder();

        String name = doctor.getUser() != null ? doctor.getUser().getFullName() : null;
        String specialty = doctor.getSpecialty() != null ? doctor.getSpecialty().getName() : null;
        String hospital = doctor.getHospital() != null ? doctor.getHospital().getName() : null;
        String experience = doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() + " năm kinh nghiệm" : null;
        String rating = doctor.getRating() != null ? String.format("%.1f ★", doctor.getRating()) : null;
        int numSchedules = doctor.getSchedules() != null ? doctor.getSchedules().size() : 0;

        if (name != null) {
            intro.append(name);
        } else {
            intro.append("Bác sĩ");
        }

        if (specialty != null) {
            intro.append(" là bác sĩ chuyên ngành ").append(specialty);
        }

        if (hospital != null) {
            intro.append(", đang công tác tại ").append(hospital);
        }

        intro.append(".");

        if (experience != null) {
            intro.append(" Với ").append(experience).append(" trong lĩnh vực");
        } else {
            intro.append(" Với nhiều năm kinh nghiệm trong lĩnh vực");
        }

        intro.append(", bác sĩ đã xây dựng được uy tín qua chất lượng khám chữa bệnh và sự tin tưởng từ bệnh nhân.");

        if (numSchedules > 0) {
            intro.append(" Hiện tại, bác sĩ có ").append(numSchedules).append(" khung giờ làm việc mỗi tuần có thể tham khảo ở phần Time slots ở bên dưới nhé.");
        }

        return intro.toString();
    }



//    private void setupCertifications(Integer doctorId) {
//        if (doctorId == null || doctorId == -1) return;
//        viewModel.loadDoctorCertifications(doctorId, 1, 10);
//        LinearLayout layoutCertifications = findViewById(R.id.layoutCertifications);
//        viewModel.certifications.observe(this, response -> {
//            if (layoutCertifications == null || response == null || response.getData() == null) return;
//            layoutCertifications.removeAllViews();
//            for (CertificationResponseDTO cert : response.getData()) {
//                String fileUrl = cert.getFileUrl();
//                ImageView imageView = new ImageView(this);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
//                params.setMargins(0, 16, 0, 16);
//                imageView.setLayoutParams(params);
//                imageView.setAdjustViewBounds(true);
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                Glide.with(this)
//                        .load(fileUrl)
//                        .into(imageView);
//                layoutCertifications.addView(imageView);
//            }
//        });
//    }
}
