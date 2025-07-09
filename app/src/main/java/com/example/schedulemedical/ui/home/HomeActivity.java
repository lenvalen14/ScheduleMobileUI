package com.example.schedulemedical.ui.home;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.DoctorAdapter;
import com.example.schedulemedical.Adapter.HospitalCardAdapter;
import com.example.schedulemedical.Adapter.SpecialtyCardAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.model.Specialty;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.filterDoctor.FilterDoctorActivity;
import com.example.schedulemedical.utils.AuthManager;
import com.example.schedulemedical.utils.NavigationHelper;

import java.util.ArrayList;
import android.widget.TextView;

public class HomeActivity extends BaseActivity {
    private HomeViewModel homeViewModel;

    private DoctorAdapter doctorAdapter;
    private HospitalCardAdapter hospitalAdapter;
    private SpecialtyCardAdapter specialtyAdapter;

    private RecyclerView recyclerDoctors, recyclerHospitals, recyclerSpecialties;
    private View homeProgressBar;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvSeeAllDoctor, tvSeeAllHospital, tvSeeAllSpecialty;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected void setupViews() {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        recyclerDoctors = findViewById(R.id.recycler_doctors);
        recyclerHospitals = findViewById(R.id.recycler_hospitals);
        recyclerSpecialties = findViewById(R.id.recycler_specialties);
        homeProgressBar = findViewById(R.id.homeProgressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvSeeAllDoctor = findViewById(R.id.tvDoctor);
        tvSeeAllHospital = findViewById(R.id.tvSeeAllHospital);
        tvSeeAllSpecialty = findViewById(R.id.tvSpecialty);
        setupAdapters();
        setupObservers();
        setupSeeAllClicks();
        if (homeViewModel.getDoctors().getValue() == null
                || homeViewModel.getHospitals().getValue() == null
                || homeViewModel.getSpecialties().getValue() == null) {
            homeViewModel.loadAllDashboardData();
        }
        swipeRefreshLayout.setOnRefreshListener(() -> homeViewModel.loadAllDashboardData());
    }

    private void setupAdapters() {
        doctorAdapter = new DoctorAdapter(this);
        recyclerDoctors.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerDoctors.setAdapter(doctorAdapter);

        doctorAdapter.setOnDoctorClickListener(new DoctorAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(DoctorResponse doctor) {
                if (doctor != null && doctor.getUser() != null) {
                    String doctorName = doctor.getUser().getFullName() != null
                            ? doctor.getUser().getFullName() : "Unknown Doctor";

                    if (doctor.getDoctorId() != null) {
                        NavigationHelper.navigateToDoctorProfile(HomeActivity.this, doctor.getUserId());
                    } else {
                        Toast.makeText(HomeActivity.this, "Không thể xem thông tin bác sĩ", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onBookAppointmentClick(DoctorResponse doctor) {
                if (doctor != null && doctor.getUser() != null) {
                    String doctorName = doctor.getUser().getFullName() != null
                            ? doctor.getUser().getFullName() : "Unknown Doctor";

                    String specialty = doctor.getSpecialty().getName() != null ? doctor.getSpecialty().getName() : "Không rõ";
                    String hospital = doctor.getHospital().getName() != null ? doctor.getHospital().getName() : "Không rõ";

                    if (doctor.getDoctorId() != null) {
                        NavigationHelper.navigateToSchedule(
                                HomeActivity.this,
                                doctor.getDoctorId(),
                                doctorName,
                                specialty,
                                hospital
                        );
                    } else {
                        Toast.makeText(HomeActivity.this, "Không thể đặt lịch với bác sĩ này", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        hospitalAdapter = new HospitalCardAdapter(this);
        recyclerHospitals.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerHospitals.setAdapter(hospitalAdapter);

        specialtyAdapter = new SpecialtyCardAdapter(this);
        recyclerSpecialties.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerSpecialties.setAdapter(specialtyAdapter);
    }

    private void setupSeeAllClicks() {
        if (tvSeeAllDoctor != null) {
            tvSeeAllDoctor.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.schedulemedical.ui.doctor.DoctorListActivity.class);
                startActivity(intent);
            });
        }
        if (tvSeeAllHospital != null) {
            tvSeeAllHospital.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.schedulemedical.ui.hospital.HospitalActivity.class);
                startActivity(intent);
            });
        }
        if (tvSeeAllSpecialty != null) {
            tvSeeAllSpecialty.setOnClickListener(v -> {
                Intent intent = new Intent(this, com.example.schedulemedical.ui.speciatly.SpecialtyActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupObservers() {
        homeViewModel.isLoading().observe(this, isLoading -> {
            if (homeProgressBar != null)
                homeProgressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
            if (swipeRefreshLayout != null)
                swipeRefreshLayout.setRefreshing(isLoading != null && isLoading);
        });
        homeViewModel.getDoctors().observe(this, doctors -> {
            if (doctors != null) doctorAdapter.updateDoctors(doctors);
        });
        homeViewModel.getHospitals().observe(this, hospitals -> {
            if (hospitals != null) hospitalAdapter.updateData(hospitals);
        });
        homeViewModel.getSpecialties().observe(this, specialties -> {
            if (specialties != null) {
                ArrayList<Specialty> list = new ArrayList<>();
                for (com.example.schedulemedical.model.dto.response.SpecialtyResponse s : specialties) {
                    list.add(new Specialty(s.getSpecialtyId(), s.getName(), s.getDescription(), 0));
                }
                specialtyAdapter.updateData(list);
            }
        });
        homeViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}