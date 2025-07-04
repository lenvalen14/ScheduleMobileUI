package com.example.schedulemedical.ui.filterDoctor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.FilterDoctorAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.utils.NavigationHelper;

import java.util.ArrayList;

public class FilterDoctorActivity extends BaseActivity {

    private FilterDoctorViewModel viewModel;
    private FilterDoctorAdapter doctorAdapter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_filter_doctor;
    }

    @Override
    protected void setupViews() {
        Toast.makeText(this, "FilterDoctorActivity started", Toast.LENGTH_SHORT).show();

        setupNavigation();

        RecyclerView recyclerView = findViewById(R.id.recycler_doctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        doctorAdapter = new FilterDoctorAdapter(this);
        recyclerView.setAdapter(doctorAdapter);

        doctorAdapter.setOnDoctorClickListener(new FilterDoctorAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(DoctorResponse doctor) {
                Toast.makeText(FilterDoctorActivity.this, "Xem bác sĩ: " + doctor.getUser().getFullName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBookAppointmentClick(DoctorResponse doctor) {
                Toast.makeText(FilterDoctorActivity.this, "Đặt lịch với: " + doctor.getUser().getFullName(), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel = new ViewModelProvider(this).get(FilterDoctorViewModel.class);
        observeViewModel();
        handleIntentExtras();
    }

    private void setupNavigation() {
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> NavigationHelper.goBack(this));
        }
    }

    private void observeViewModel() {
        viewModel.filteredDoctors.observe(this, response -> {
            if (response.getData() != null) {
                doctorAdapter.updateDoctors(response.getData());
            } else {
                Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleIntentExtras() {
        Bundle filterData = getIntent().getBundleExtra(NavigationHelper.EXTRA_FILTER_DATA);
        if (filterData != null) {
            Integer specialtyId = filterData.getInt("specialtyId", -1);
            Float minRating = filterData.getFloat("minRating", 0f);
            Integer hospitalId = filterData.getInt("hospitalId", -1);

            viewModel.filterDoctors(
                    specialtyId != -1 ? specialtyId : null,
                    minRating > 0f ? minRating : null,
                    hospitalId != -1 ? hospitalId : null,
                    1,
                    20
            );
        }
    }
}
