package com.example.schedulemedical.ui.filterDoctor;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.FilterDoctorAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.repository.DoctorFilterRepository;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.ui.base.BaseActivity;
import com.example.schedulemedical.ui.doctorprofile.DoctorProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class FilterDoctorActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private FilterDoctorAdapter adapter;
    private FilterDoctorViewModel viewModel;
    private DoctorFilterOptionsViewModel doctorFilterOptionsViewModel;

    private List<SpecialtyResponse> specialtyList = new ArrayList<>();
    private List<HospitalResponse> hospitalList = new ArrayList<>();

    private Integer selectedSpecialtyId = null;
    private Integer selectedHospitalId = null;
    private Float selectedRating = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_filter_doctor;
    }

    @Override
    protected void setupViews() {
        setupNavigation();

        recyclerView = findViewById(R.id.rvDoctors);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FilterDoctorAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.setOnDoctorClickListener(new FilterDoctorAdapter.OnDoctorClickListener() {
            @Override
            public void onDoctorClick(DoctorResponse doctor) {
                Intent intent = new Intent(FilterDoctorActivity.this, DoctorProfileActivity.class);
                Log.d("Doctor View", "for doctorId: " + doctor.getDoctorId());
                intent.putExtra("DOCTOR_ID", doctor.getDoctorId());
                startActivity(intent);
            }

            @Override
            public void onBookAppointmentClick(DoctorResponse doctor) {
                Toast.makeText(FilterDoctorActivity.this,
                        "Đặt lịch với bác sĩ: " + doctor.getUser().getFullName(),
                        Toast.LENGTH_SHORT).show();

                // TODO: Nếu có màn đặt lịch, mở ở đây
                // Intent intent = new Intent(FilterDoctorActivity.this, BookingActivity.class);
                // intent.putExtra("DOCTOR_ID", doctor.getId());
                // startActivity(intent);
            }
        });

        viewModel = new ViewModelProvider(this).get(FilterDoctorViewModel.class);

        DoctorFilterRepository repository = new DoctorFilterRepository(
                ApiClient.getDoctorApiService(),
                ApiClient.getHospitalApiService()
        );
        DoctorFilterOptionsViewModelFactory factory = new DoctorFilterOptionsViewModelFactory(repository);
        doctorFilterOptionsViewModel = new ViewModelProvider(this, factory).get(DoctorFilterOptionsViewModel.class);

        doctorFilterOptionsViewModel.specialties.observe(this, specialties -> {
            if (specialties != null) {
                specialtyList.clear();
                specialtyList.addAll(specialties);
            }
        });

        doctorFilterOptionsViewModel.hospitals.observe(this, hospitals -> {
            if (hospitals != null) {
                hospitalList.clear();
                hospitalList.addAll(hospitals);
            }
        });

        observeData();

        viewModel.filterDoctors(null, null, null, 1, 20); // Load initial

        ImageView filterButton = findViewById(R.id.btnFilter);
        filterButton.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void setupNavigation() {
        ImageView backButton = findViewById(R.id.btnBack);
        if (backButton != null) {
            backButton.setOnClickListener(view -> onBackPressed());
        }
    }

    private void observeData() {
        viewModel.filteredDoctors.observe(this, response -> {
            if (response != null && response.getData() != null) {
                adapter.updateDoctors(response.getData().getData());
            } else {
                Toast.makeText(this, "Không lấy được danh sách bác sĩ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_doctor_filter, null);
        bottomSheetDialog.setContentView(view);

        Spinner spinnerSpecialty = view.findViewById(R.id.spinnerSpecialty);
        Spinner spinnerHospital = view.findViewById(R.id.spinnerHospital);
        SeekBar seekBarRating = view.findViewById(R.id.seekBarRating);
        TextView tvRatingValue = view.findViewById(R.id.tvRatingValue);
        TextView btnApply = view.findViewById(R.id.btnApplyFilter);
        TextView btnCancel = view.findViewById(R.id.btnCancel);
        TextView tvClear = view.findViewById(R.id.tvClearFilters);

        List<String> specialtyNames = new ArrayList<>();
        specialtyNames.add("Tất cả");
        for (SpecialtyResponse s : specialtyList) {
            specialtyNames.add(s.getName());
        }
        ArrayAdapter<String> specialtyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, specialtyNames);
        specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecialty.setAdapter(specialtyAdapter);

        List<String> hospitalNames = new ArrayList<>();
        hospitalNames.add("Tất cả");
        for (HospitalResponse h : hospitalList) {
            hospitalNames.add(h.getName());
        }
        ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hospitalNames);
        hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHospital.setAdapter(hospitalAdapter);

        seekBarRating.setMax(50);
        seekBarRating.setProgress(selectedRating != null ? (int) (selectedRating * 10) : 0);
        tvRatingValue.setText(selectedRating != null ? "Từ " + selectedRating + " sao" : "Tất cả đánh giá");

        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float rating = progress / 10.0f;
                tvRatingValue.setText(progress > 0 ? "Từ " + rating + " sao" : "Tất cả đánh giá");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        tvClear.setOnClickListener(v -> {
            spinnerSpecialty.setSelection(0);
            spinnerHospital.setSelection(0);
            seekBarRating.setProgress(0);
        });

        btnApply.setOnClickListener(v -> {
            int specialtyIndex = spinnerSpecialty.getSelectedItemPosition();
            selectedSpecialtyId = specialtyIndex == 0 ? null : specialtyList.get(specialtyIndex - 1).getSpecialtyId();

            int hospitalIndex = spinnerHospital.getSelectedItemPosition();
            selectedHospitalId = hospitalIndex == 0 ? null : hospitalList.get(hospitalIndex - 1).getHospitalId();

            int ratingProgress = seekBarRating.getProgress();
            selectedRating = ratingProgress > 0 ? ratingProgress / 10.0f : null;

            viewModel.filterDoctors(selectedHospitalId, selectedRating, selectedSpecialtyId, 1, 20);
            bottomSheetDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }
}
