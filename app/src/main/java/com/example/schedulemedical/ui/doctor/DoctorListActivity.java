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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        setupSearchListener();
        setupProgressDialog();

        // Initialize API service
        ApiClient.init(this);
        doctorApiService = ApiClient.getDoctorApiService();

        // Load doctors
        loadDoctors();
    }

    private void initializeViews() {
        ivBack = findViewById(R.id.ivBack);
//        etSearch = findViewById(R.id.etSearch);
//        ivFilter = findViewById(R.id.ivFilter);
//        rvDoctors = findViewById(R.id.rvDoctors);
//        tvNoResults = findViewById(R.id.tvNoResults);
        layoutLoading = findViewById(R.id.layoutLoading);

        allDoctors = new ArrayList<>();
    }

    private void setupRecyclerView() {
        doctorAdapter = new DoctorAdapter(this, allDoctors);
        doctorAdapter.setOnDoctorClickListener(this);

        rvDoctors.setLayoutManager(new LinearLayoutManager(this));
        rvDoctors.setAdapter(doctorAdapter);
    }

    private void setupClickListeners() {
        // Back button
        ivBack.setOnClickListener(v -> finish());

        // Filter button
        ivFilter.setOnClickListener(v -> showFilterDialog());
    }

    private void setupSearchListener() {
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

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang tải danh sách bác sĩ...");
        progressDialog.setCancelable(false);
    }

    private void loadDoctors() {
        showLoading();

        // TODO: Call API to get all doctors with current filters
        // doctorApiService.getAllDoctors(
        //     1, // page
        //     50, // limit
        //     currentSpecialty,
        //     null, // hospitalId
        //     currentMinRating > 0 ? currentMinRating : null,
        //     null, // experience
        //     currentSortBy,
        //     currentSortOrder
        // ).enqueue(new Callback<ApiResponse<Object>>() {

        // Mock for now - create empty list for testing
        List<DoctorResponse> mockDoctors = new ArrayList<>();
        allDoctors.clear();
        allDoctors.addAll(mockDoctors);
        doctorAdapter.updateDoctors(allDoctors);
        updateNoResultsVisibility();
        hideLoading();

        /*
        // Original API call - commented out until getAllDoctors method is implemented
        doctorApiService.getAllDoctors(parameters).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Object data = response.body().getData();
                        Gson gson = new Gson();
                        String jsonString = gson.toJson(data);
                        Type listType = new TypeToken<List<DoctorResponse>>(){}.getType();
                        List<DoctorResponse> doctors = gson.fromJson(jsonString, listType);
                        if (doctors != null) {
                            allDoctors.clear();
                            allDoctors.addAll(doctors);
                            doctorAdapter.updateDoctors(allDoctors);
                            updateNoResultsVisibility();
                        } else {
                            showError("Không có dữ liệu bác sĩ");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing doctors data", e);
                        showError("Lỗi xử lý dữ liệu");
                    }
                } else {
                    showError("Không thể tải danh sách bác sĩ");
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Failed to load doctors", t);
                showError("Lỗi kết nối: " + t.getMessage());
            }
        });
        */
    }

    private void showFilterDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_doctor_filter, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Setup filter options
        setupSpecialtyFilter(bottomSheetView);
        setupRatingFilter(bottomSheetView);
        setupFilterActions(bottomSheetView, bottomSheetDialog);

        bottomSheetDialog.show();
    }

    private void setupSpecialtyFilter(View bottomSheetView) {
        // ChipGroup chipGroupSpecialty = bottomSheetView.findViewById(R.id.chipGroupSpecialty); // Not in layout

        // TODO: Use the actual Spinner instead
        // Spinner spinnerSpecialty = bottomSheetView.findViewById(R.id.spinnerSpecialty);

        // For now, comment out the chip group logic
        /*
        if (chipGroupSpecialty != null) {
            chipGroupSpecialty.removeAllViews();

            // Add "All Specialties" chip
            Chip allChip = new Chip(this);
            allChip.setText("All Specialties");
            allChip.setCheckable(true);
            allChip.setChecked(currentSpecialty == null);
            chipGroupSpecialty.addView(allChip);

            // Add specialty chips
            for (String specialty : specialties) {
                Chip chip = new Chip(this);
                chip.setText(specialty);
                chip.setCheckable(true);
                chip.setChecked(specialty.equals(currentSpecialty));
                chipGroupSpecialty.addView(chip);
            }

            // Set selection listener
            chipGroupSpecialty.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip selectedChip = group.findViewById(checkedIds.get(0));
                    if (selectedChip != null) {
                        String selectedText = selectedChip.getText().toString();
                        currentSpecialty = "All Specialties".equals(selectedText) ? null : selectedText;
                    }
                }
            });
        }
        */
    }

    private void setupRatingFilter(View bottomSheetView) {
        // ChipGroup chipGroupRating = bottomSheetView.findViewById(R.id.chipGroupRating); // Not in layout

        // TODO: Use the actual SeekBar instead
        // SeekBar seekBarRating = bottomSheetView.findViewById(R.id.seekBarRating);

        // For now, comment out the chip group logic
        /*
        if (chipGroupRating != null) {
            chipGroupRating.removeAllViews();

            for (int i = 0; i < ratingOptions.length; i++) {
                Chip chip = new Chip(this);
                chip.setText(ratingOptions[i]);
                chip.setCheckable(true);
                chip.setChecked(currentMinRating == ratingValues[i]);
                chip.setTag(ratingValues[i]);
                chipGroupRating.addView(chip);
            }

            // Set selection listener
            chipGroupRating.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (!checkedIds.isEmpty()) {
                    Chip selectedChip = group.findViewById(checkedIds.get(0));
                    if (selectedChip != null && selectedChip.getTag() != null) {
                        currentMinRating = (Float) selectedChip.getTag();
                    }
                }
            });
        }
        */
    }

    private void setupFilterActions(View bottomSheetView, BottomSheetDialog dialog) {
        View btnApplyFilter = bottomSheetView.findViewById(R.id.btnApplyFilter);
        // View btnClearFilter = bottomSheetView.findViewById(R.id.btnClearFilter); // Not in layout
        View btnCancel = bottomSheetView.findViewById(R.id.btnCancel);

        if (btnApplyFilter != null) {
            btnApplyFilter.setOnClickListener(v -> {
                dialog.dismiss();
                applyFilters();
            });
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                dialog.dismiss();
            });
        }

        // if (btnClearFilter != null) {
        //     btnClearFilter.setOnClickListener(v -> {
        //         currentSpecialty = null;
        //         currentMinRating = 0f;
        //         dialog.dismiss();
        //         applyFilters();
        //     });
        // }
    }

    private void applyFilters() {
        // Apply local filters first
        if (doctorAdapter != null) {
            if (currentSpecialty != null) {
                doctorAdapter.filterBySpecialty(currentSpecialty);
            } else if (currentMinRating > 0) {
                doctorAdapter.filterByRating(currentMinRating);
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
                rvDoctors.setVisibility(View.GONE);
            } else {
                tvNoResults.setVisibility(View.GONE);
                rvDoctors.setVisibility(View.VISIBLE);
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
        // Intent intent = new Intent(this, DoctorProfileActivity.class); // TODO: Create DoctorProfileActivity
        // intent.putExtra("doctorId", doctor.getId());
        // intent.putExtra("doctorName", doctor.getFullName());
        // startActivity(intent);
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