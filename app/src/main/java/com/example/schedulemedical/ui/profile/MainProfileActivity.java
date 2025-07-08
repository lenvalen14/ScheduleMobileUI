package com.example.schedulemedical.ui.profile;

import static com.example.schedulemedical.utils.NavigationHelper.ROLE;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AuthApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.ProfileResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.doctorprofile.DoctorEditProfileActivity;
import com.example.schedulemedical.ui.doctorprofile.DoctorViewModel;
import com.example.schedulemedical.ui.forgotPassword.ResetPasswordActivity;
import com.example.schedulemedical.ui.schedule.ManageSchedule;
import com.example.schedulemedical.ui.schedule.MyScheduledActivity;
import com.example.schedulemedical.utils.AuthManager;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainProfileActivity extends BaseActivity {

    private DoctorViewModel viewModel;
    private AuthManager authManager;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main_profile;
    }

    @Override
    protected void setupViews() {

        authManager = new AuthManager(this);

        // ✅ Khởi tạo ViewModel ở đây
        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);

        String role = getIntent().getStringExtra(ROLE);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Avatar và thông tin người dùng
        CircleImageView ivAvatar = findViewById(R.id.ivAvatar);
        TextView tvFullName = findViewById(R.id.tvFullName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvPhone = findViewById(R.id.tvPhone);

        AuthApiService authApiService = ApiClient.getAuthApiService();
        authApiService.getProfile().enqueue(new Callback<ApiResponse<ProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileResponse>> call, Response<ApiResponse<ProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ProfileResponse profile = response.body().getData();
                    tvFullName.setText(profile.getFullName() != null ? profile.getFullName() : "");
                    tvEmail.setText(profile.getEmail() != null ? profile.getEmail() : "");
                    tvPhone.setText(profile.getPhone() != null ? profile.getPhone() : "");

                    String avatarUrl = profile.getAvatar();
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(MainProfileActivity.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.sample_profile_image)
                                .into(ivAvatar);
                    }
                } else {
                    fallbackProfile(tvFullName, tvEmail, tvPhone, ivAvatar);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileResponse>> call, Throwable t) {
                fallbackProfile(tvFullName, tvEmail, tvPhone, ivAvatar);
            }
        });

        setupMenuItem(R.id.itemMyAppointments, R.drawable.ic_calendar, "Lịch khám của tôi");
        setupMenuItem(R.id.itemPersonalDetails, R.drawable.ic_person_outline, "Thông tin cá nhân");

        View itemSchedule = findViewById(R.id.itemSchedule);
        if ("DOCTOR".equalsIgnoreCase(role)) {
            setupMenuItem(R.id.itemSchedule, R.drawable.ic_calendar, "Lịch biểu của tôi");
            itemSchedule.setVisibility(View.VISIBLE);
        } else {
            itemSchedule.setVisibility(View.GONE);
        }

        setupMenuItem(R.id.itemPassword, R.drawable.ic_password, "Đổi mật khẩu");
        setupMenuItem(R.id.itemSettings, R.drawable.ic_settings, "Cài đặt");
        setupMenuItem(R.id.itemLogout, R.drawable.ic_logout, "Đăng xuất");

        // Xử lý click
        findViewById(R.id.itemSchedule).setOnClickListener(v -> {
            int userId = authManager.getUserId();
            viewModel.loadDoctorProfileByUserId(userId);

            viewModel.doctorProfile.observe(this, doctor -> {
                if (doctor != null) {
                    Intent intent = new Intent(this, ManageSchedule.class);
                    intent.putExtra("doctorId", doctor.getDoctorId());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Không tìm thấy hồ sơ bác sĩ", Toast.LENGTH_SHORT).show();
                }
            });
        });


        findViewById(R.id.itemMyAppointments).setOnClickListener(v -> {
            Intent intent = new Intent(this, MyScheduledActivity.class);
            intent.putExtra("role", role);

            if ("DOCTOR".equalsIgnoreCase(role)) {
                int userId = authManager.getUserId();
                viewModel.loadDoctorProfileByUserId(userId);

                viewModel.doctorProfile.observe(this, doctor -> {
                    if (doctor != null) {
                        intent.putExtra("doctorId", doctor.getDoctorId());
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Không tìm thấy hồ sơ bác sĩ", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                startActivity(intent);
            }
        });

        findViewById(R.id.itemPersonalDetails).setOnClickListener(v -> {
            if ("DOCTOR".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, DoctorEditProfileActivity.class));
            } else {
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });

        findViewById(R.id.itemPassword).setOnClickListener(v ->
                startActivity(new Intent(this, ResetPasswordActivity.class)));

        findViewById(R.id.itemSettings).setOnClickListener(v ->
                Toast.makeText(this, "Chức năng Cài đặt sẽ sớm có!", Toast.LENGTH_SHORT).show());

        findViewById(R.id.itemLogout).setOnClickListener(v -> {
            new AuthManager(this).logout(new AuthManager.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    Intent intent = new Intent(MainProfileActivity.this, com.example.schedulemedical.ui.login.LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(MainProfileActivity.this, "Logout failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupMenuItem(int itemId, int iconRes, String label) {
        View item = findViewById(itemId);
        ImageView ivIcon = item.findViewById(R.id.ivIcon);
        TextView tvLabel = item.findViewById(R.id.tvLabel);
        ivIcon.setImageResource(iconRes);
        tvLabel.setText(label);
    }

    private void fallbackProfile(TextView tvFullName, TextView tvEmail, TextView tvPhone, ImageView ivAvatar) {
        AuthManager authManager = new AuthManager(this);
        String fullName = authManager.getUserName();
        String email = authManager.getUserEmail();

        tvFullName.setText(fullName != null ? fullName : "");
        tvEmail.setText(email != null ? email : "");
        tvPhone.setText("");
        Glide.with(this).load(R.drawable.sample_profile_image).into(ivAvatar);
    }
}
