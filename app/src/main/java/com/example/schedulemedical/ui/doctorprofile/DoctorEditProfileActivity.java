package com.example.schedulemedical.ui.doctorprofile;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.schedulemedical.R;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;
import androidx.lifecycle.ViewModelProvider;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.utils.AuthManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DoctorEditProfileActivity extends BaseActivity {

    private DoctorViewModel viewModel;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_edit_doctor_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("DoctorEditProfile", "onCreate called");
        Toast.makeText(this, "onCreate DoctorEditProfile", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void setupViews() {
        Log.d("DoctorEditProfile", "setupViews called");
        Toast.makeText(this, "setupViews DoctorEditProfile", Toast.LENGTH_SHORT).show();
        setupNavigation();
        loadDoctorProfileByUserId();
    }

    private void setupNavigation() {
        // Back button
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                NavigationHelper.goBack(this);
            });
        }

        // Bottom navigation xử lý đúng chuẩn
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    NavigationHelper.navigateToHome(this);
                    return true;
                }
                // Xử lý các item khác nếu cần
                return false;
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

    private void loadDoctorProfileByUserId() {
        AuthManager authManager = new AuthManager(this);
        int userId = authManager.getUserId() != null ? authManager.getUserId() : -1;
        Log.d("DoctorEditProfile", "userId: " + userId);
        Toast.makeText(this, "userId: " + userId, Toast.LENGTH_SHORT).show();
        if (userId == -1) {
            Toast.makeText(this, "Không lấy được userId, quay về Home", Toast.LENGTH_LONG).show();
            NavigationHelper.navigateToHome(this);
            return;
        }
        viewModel = new ViewModelProvider(this).get(DoctorViewModel.class);
        viewModel.loadDoctorProfileByUserId(userId);
        viewModel.doctorProfile.observe(this, doctor -> {
            if (doctor != null) {
                mapDoctorProfileToUI(doctor);
            }
        });
    }

    private void mapDoctorProfileToUI(DoctorResponse doctor) {
        ((TextView) findViewById(R.id.et_first_name)).setText(doctor.getUser() != null ? doctor.getUser().getFullName() : "");
        ((TextView) findViewById(R.id.et_bio)).setText(doctor.getBio() != null ? doctor.getBio() : "");
        ((TextView) findViewById(R.id.et_education)).setText(doctor.getEducation() != null ? doctor.getEducation() : "");
        ((TextView) findViewById(R.id.et_experience)).setText(doctor.getYearsOfExperience() != null ? doctor.getYearsOfExperience() : "");
        ((android.widget.AutoCompleteTextView) findViewById(R.id.actv_specialty)).setText(
            doctor.getSpecialty() != null && doctor.getSpecialty().getName() != null ? doctor.getSpecialty().getName() : "", false);
        ((android.widget.AutoCompleteTextView) findViewById(R.id.actv_hospital)).setText(
            doctor.getHospital() != null && doctor.getHospital().getName() != null ? doctor.getHospital().getName() : "", false);
        ((TextView) findViewById(R.id.et_clinic)).setText(doctor.getClinic() != null ? doctor.getClinic() : "");
        ((TextView) findViewById(R.id.et_slot_capacity)).setText(doctor.getSlotCapacity() != null ? String.valueOf(doctor.getSlotCapacity()) : "");
    }
}