package com.example.schedulemedical.ui.doctor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.DoctorAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.ui.booking.BookingActivity;
import com.example.schedulemedical.ui.doctorprofile.DoctorProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
import com.example.schedulemedical.utils.NavigationHelper;

public class DoctorListActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorClickListener {
    private static final String TAG = "DoctorListActivity";

    // UI Components
    private ImageView ivBack;
    private EditText etSearch;
    private ImageView ivFilter;
    private RecyclerView rvDoctors;
    private TextView tvNoResults;
    private LinearLayout layoutLoading;

    // Data and Adapters
    private DoctorAdapter doctorAdapter;
    private List<DoctorResponse> allDoctors;
    private DoctorApiService doctorApiService;
    private ProgressDialog progressDialog;

    // Filter states
    private String currentSpecialty = null;
    private float currentMinRating = 0f;
    private String currentSortBy = "rating";
    private String currentSortOrder = "desc";

    // Thêm biến lưu filter hiện tại
    private Integer selectedSpecialtyId = null;
    private Integer selectedHospitalId = null;
    private Float selectedMinRating = null;

    // Predefined specialties
    private String[] specialties = {
        "Cardiology", "Dermatology", "Neurology", "Orthopedics",
        "Pediatrics", "Psychiatry", "General Medicine", "Surgery",
        "Gynecology", "Ophthalmology", "ENT", "Radiology"
    };

    private String[] ratingOptions = {"All Ratings", "4+ Stars", "3+ Stars", "2+ Stars"};
    private float[] ratingValues = {0f, 4f, 3f, 2f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_doctor);

        initializeViews();
        setupRecyclerView();
        setupClickListeners();
        // setupSearchListener();  // Skip since etSearch doesn't exist
        setupProgressDialog();

        // Initialize API service
        ApiClient.init(this);
        doctorApiService = ApiClient.getDoctorApiService();

        // Load doctors
        loadDoctors();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.btnBack);  // Use btnBack instead of ivBack
        ivFilter = findViewById(R.id.btnFilter); // Đảm bảo ánh xạ đúng
        // etSearch = findViewById(R.id.etSearch);  // View doesn't exist in layout
        // ivFilter = findViewById(R.id.ivFilter);  // View doesn't exist in layout
        rvDoctors = findViewById(R.id.rvDoctors);  // Use correct ID from layout
        // tvNoResults = findViewById(R.id.tvNoResults);  // View doesn't exist in layout
        // layoutLoading = findViewById(R.id.layoutLoading);  // View doesn't exist in layout

        allDoctors = new ArrayList<>();
    }

    private void setupRecyclerView() {
        if (rvDoctors != null) {
            doctorAdapter = new DoctorAdapter(this);
            doctorAdapter.setOnDoctorClickListener(this);

            rvDoctors.setLayoutManager(new LinearLayoutManager(this));
            rvDoctors.setAdapter(doctorAdapter);
        } else {
            Log.e(TAG, "RecyclerView is null - check layout and view IDs");
        }
    }

    private void setupClickListeners() {
        // Back button
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        // Filter button
        if (ivFilter != null) {
            ivFilter.setOnClickListener(v -> showFilterDialog());
        }
    }

    private void setupSearchListener() {
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if (doctorAdapter != null) {
                        doctorAdapter.filter(s.toString());
                        updateNoResultsVisibility();
                    }
                }
            });
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải danh sách bác sĩ...");
        progressDialog.setCancelable(false);
    }

    private void loadDoctors() {
        showLoading();
        doctorApiService.filterDoctors(
            selectedSpecialtyId,
            selectedMinRating,
            selectedHospitalId,
            1,    // page
            50    // limit
        ).enqueue(new retrofit2.Callback<DoctorListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<DoctorListResponse> call, retrofit2.Response<DoctorListResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<DoctorResponse> doctors = response.body().getData();
                    allDoctors.clear();
                    allDoctors.addAll(doctors);
                    Log.d(TAG, "Số lượng doctor: " + allDoctors.size());
                    doctorAdapter.updateDoctors(allDoctors);
                    updateNoResultsVisibility();
                } else {
                    showError("Không thể tải danh sách bác sĩ");
                }
            }
            @Override
            public void onFailure(retrofit2.Call<DoctorListResponse> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Failed to load doctors", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void showFilterDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_doctor_filter, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Spinner spinnerSpecialty = bottomSheetView.findViewById(R.id.spinnerSpecialty);
        Spinner spinnerHospital = bottomSheetView.findViewById(R.id.spinnerHospital);
        SeekBar seekBarRating = bottomSheetView.findViewById(R.id.seekBarRating);
        TextView tvRatingValue = bottomSheetView.findViewById(R.id.tvRatingValue);
        Button btnApply = bottomSheetView.findViewById(R.id.btnApplyFilter);
        Button btnCancel = bottomSheetView.findViewById(R.id.btnCancel);
        TextView tvClearFilters = bottomSheetView.findViewById(R.id.tvClearFilters);

        // Reset list trước khi load
        List<Integer> specialtyIds = new ArrayList<>();
        List<String> specialtyNames = new ArrayList<>();
        List<Integer> hospitalIds = new ArrayList<>();
        List<String> hospitalNames = new ArrayList<>();

        specialtyNames.add("Tất cả"); specialtyIds.add(null);
        hospitalNames.add("Tất cả"); hospitalIds.add(null);

        // Lấy danh sách chuyên khoa từ API
        doctorApiService.getAllSpecialties(1, 100).enqueue(new retrofit2.Callback<com.example.schedulemedical.model.dto.response.ApiResponse<List<com.example.schedulemedical.model.dto.response.SpecialtyResponse>>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.schedulemedical.model.dto.response.ApiResponse<List<com.example.schedulemedical.model.dto.response.SpecialtyResponse>>> call, retrofit2.Response<com.example.schedulemedical.model.dto.response.ApiResponse<List<com.example.schedulemedical.model.dto.response.SpecialtyResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    for (com.example.schedulemedical.model.dto.response.SpecialtyResponse s : response.body().getData()) {
                        specialtyNames.add(s.getName());
                        specialtyIds.add(s.getSpecialtyId());
                    }
                }
                ArrayAdapter<String> specialtyAdapter = new ArrayAdapter<>(DoctorListActivity.this, android.R.layout.simple_spinner_item, specialtyNames);
                specialtyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSpecialty.setAdapter(specialtyAdapter);
                spinnerSpecialty.setSelection(selectedSpecialtyId == null ? 0 : specialtyIds.indexOf(selectedSpecialtyId));
            }
            @Override public void onFailure(retrofit2.Call<com.example.schedulemedical.model.dto.response.ApiResponse<List<com.example.schedulemedical.model.dto.response.SpecialtyResponse>>> call, Throwable t) {}
        });

        // Lấy danh sách bệnh viện từ API (nếu có), nếu chưa có thì mock tạm
        // TODO: Thay thế bằng API thực tế nếu có
        hospitalNames.add("Bệnh viện Bạch Mai"); hospitalIds.add(1);
        hospitalNames.add("Bệnh viện Chợ Rẫy"); hospitalIds.add(2);
        ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hospitalNames);
        hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHospital.setAdapter(hospitalAdapter);
        spinnerHospital.setSelection(selectedHospitalId == null ? 0 : hospitalIds.indexOf(selectedHospitalId));

        // SeekBar rating
        seekBarRating.setMax(40); // 0-40 tương ứng 0-4.0
        seekBarRating.setProgress(selectedMinRating == null ? 0 : (int)(selectedMinRating * 10));
        tvRatingValue.setText(selectedMinRating == null ? "All ratings" : (selectedMinRating + "+ stars"));
        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float rating = progress / 10f;
                tvRatingValue.setText(progress == 0 ? "All ratings" : (rating + "+ stars"));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnApply.setOnClickListener(v -> {
            int specialtyPos = spinnerSpecialty.getSelectedItemPosition();
            selectedSpecialtyId = specialtyIds.get(specialtyPos);
            int hospitalPos = spinnerHospital.getSelectedItemPosition();
            selectedHospitalId = hospitalIds.get(hospitalPos);
            int progress = seekBarRating.getProgress();
            selectedMinRating = progress == 0 ? null : progress / 10f;
            bottomSheetDialog.dismiss();
            loadDoctors();
        });
        tvClearFilters.setOnClickListener(v -> {
            selectedSpecialtyId = null;
            selectedHospitalId = null;
            selectedMinRating = null;
            bottomSheetDialog.dismiss();
            loadDoctors();
        });
        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void applyFilters() {
        // Apply local filters first
        if (doctorAdapter != null) {
            if (currentSpecialty != null) {
                // doctorAdapter.filterBySpecialty(currentSpecialty);  // Method doesn't exist
                loadDoctors();  // Reload instead
            } else if (currentMinRating > 0) {
                // doctorAdapter.filterByRating(currentMinRating);  // Method doesn't exist
                loadDoctors();  // Reload instead
            } else {
                // If no local filters, reload from server
                loadDoctors();
                return;
            }
        }

        updateNoResultsVisibility();

        // Optionally reload from server for more accurate results
        // loadDoctors();
    }

    private void updateNoResultsVisibility() {
        if (tvNoResults != null && doctorAdapter != null) {
            if (doctorAdapter.getItemCount() == 0) {
                tvNoResults.setVisibility(View.VISIBLE);
                if (rvDoctors != null) {
                    rvDoctors.setVisibility(View.GONE);
                }
            } else {
                tvNoResults.setVisibility(View.GONE);
                if (rvDoctors != null) {
                    rvDoctors.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void showLoading() {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(View.VISIBLE);
        }
        if (rvDoctors != null) {
            rvDoctors.setVisibility(View.GONE);
        }
        if (tvNoResults != null) {
            tvNoResults.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(View.GONE);
        }
        if (rvDoctors != null) {
            rvDoctors.setVisibility(View.VISIBLE);
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (tvNoResults != null) {
            tvNoResults.setText(message);
            tvNoResults.setVisibility(View.VISIBLE);
        }
        if (rvDoctors != null) {
            rvDoctors.setVisibility(View.GONE);
        }
    }

    // DoctorAdapter.OnDoctorClickListener implementation
    @Override
    public void onDoctorClick(DoctorResponse doctor) {
        // Navigate to doctor profile
        Intent intent = new Intent(this, DoctorProfileActivity.class);
        intent.putExtra(NavigationHelper.EXTRA_DOCTOR_ID, doctor.getDoctorId());
        intent.putExtra("doctorName", doctor.getUser().getFullName());
        startActivity(intent);
    }

    @Override
    public void onBookAppointmentClick(DoctorResponse doctor) {
        // Navigate to booking activity
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("doctorId", doctor.getDoctorId());
        intent.putExtra("doctorName", doctor.getUser().getFullName());
        intent.putExtra("specialty", doctor.getSpecialty().getName());
        intent.putExtra("hospitalName", doctor.getHospital().getName());
//        intent.putExtra("consultationFee", doctor.getConsultationFee());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}