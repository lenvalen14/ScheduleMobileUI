package com.example.schedulemedical.ui.booking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.SpecialtyAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.model.Specialty;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.ui.booking.BookingWizardActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialtySelectionFragment extends Fragment implements SpecialtyAdapter.OnSpecialtySelectedListener {
    private static final String TAG = "SpecialtySelectionFragment";
    private static final String ARG_BOOKING_DATA = "booking_data";

    // UI Components
    private RecyclerView rvSpecialties;
    private ProgressBar progressBar;
    private TextView tvNoSpecialties;

    // Data
    private BookingWizardActivity.BookingData bookingData;
    private List<Specialty> specialtiesList;
    private SpecialtyAdapter specialtyAdapter;

    // Services
    private DoctorApiService doctorApiService;

    public static SpecialtySelectionFragment newInstance(BookingWizardActivity.BookingData bookingData) {
        SpecialtySelectionFragment fragment = new SpecialtySelectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOKING_DATA, bookingData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingData = (BookingWizardActivity.BookingData) getArguments().getSerializable(ARG_BOOKING_DATA);
        }

        ApiClient.init(requireContext());
        doctorApiService = ApiClient.getDoctorApiService();
        specialtiesList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_specialty_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        
        // Check if specialty is already selected
        if (bookingData.specialtyId != null && bookingData.specialtyId > 0) {
            // Specialty is already selected, show selected specialty info
            showSelectedSpecialtyInfo();
        } else {
            // Load specialties list
            loadSpecialties();
        }
    }

    private void initializeViews(View view) {
        rvSpecialties = view.findViewById(R.id.rvSpecialties);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoSpecialties = view.findViewById(R.id.tvNoSpecialties);
    }

    private void setupRecyclerView() {
        specialtyAdapter = new SpecialtyAdapter(requireContext(), specialtiesList);
        specialtyAdapter.setOnSpecialtySelectedListener(this);
        rvSpecialties.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvSpecialties.setAdapter(specialtyAdapter);
    }

    private void loadSpecialties() {
        showLoading(true);
        
        // Check if we have hospital context
        Integer hospitalId = null;
        if (getActivity() instanceof BookingWizardActivity) {
            hospitalId = ((BookingWizardActivity) getActivity()).getHospitalId();
        }
        
        if (hospitalId != null && hospitalId > 0) {
            // Load specialties for specific hospital
            loadSpecialtiesByHospital(hospitalId);
        } else {
            // Load all specialties
            loadAllSpecialties();
        }
    }
    
    private void loadAllSpecialties() {
        doctorApiService.getAllSpecialties(1, 50).enqueue(new Callback<ApiResponse<List<SpecialtyResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                showLoading(false);
                Log.d(TAG, "Specialties API call success");
                Log.d(TAG, "Response body: " + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    List<SpecialtyResponse> responseList = response.body().getData();
                    Log.d(TAG, "Specialties count: " + responseList.size());
                    
                    List<Specialty> specialties = new ArrayList<>();

                    for (SpecialtyResponse res : responseList) {
                        Log.d(TAG, "Processing specialty: " + res.getName() + " (ID: " + res.getSpecialtyId() + ")");
                        Log.d(TAG, "Raw specialty data - name: " + res.getName() + ", id: " + res.getSpecialtyId() + ", description: " + res.getDescription() + ", doctorCount: " + res.getDoctorCount());
                        
                        Specialty specialty = new Specialty(
                                res.getSpecialtyId(),
                                res.getName(),
                                res.getDescription(),
                                res.getDoctorCount()
                        );
                        
                        Log.d(TAG, "Created Specialty object - name: " + specialty.getName() + ", id: " + specialty.getSpecialtyId());
                        specialties.add(specialty);
                    }

                    updateSpecialtiesList(specialties);
                } else {
                    Log.e(TAG, "API call failed or response is null. Response code: " + response.code());
                    showError("Không thể tải danh sách chuyên khoa");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SpecialtyResponse>>> call, Throwable t) {
                Log.e(TAG, "Specialties API call failed", t);
                showLoading(false);
                showError("Lỗi kết nối mạng");
            }
        });
    }
    
    private void loadSpecialtiesByHospital(Integer hospitalId) {
        // For now, we'll load all specialties and filter by hospital
        // In the future, you might want to create a specific API endpoint for this
        doctorApiService.getAllSpecialties(1, 50).enqueue(new Callback<ApiResponse<List<SpecialtyResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                showLoading(false);
                Log.d(TAG, "Specialties by hospital API call success");
                Log.d(TAG, "Response body: " + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    List<SpecialtyResponse> responseList = response.body().getData();
                    Log.d(TAG, "All specialties count: " + responseList.size());
                    
                    // Filter specialties that have doctors in this hospital
                    List<Specialty> specialties = new ArrayList<>();
                    
                    for (SpecialtyResponse res : responseList) {
                        Log.d(TAG, "Processing specialty: " + res.getName() + " (ID: " + res.getSpecialtyId() + ")");
                        
                        // Check if this specialty has doctors in the hospital
                        checkSpecialtyInHospital(res, hospitalId, specialties);
                    }
                    
                    // If no specialties found, show message
                    if (specialties.isEmpty()) {
                        showNoSpecialtiesMessage();
                    } else {
                        updateSpecialtiesList(specialties);
                    }
                } else {
                    Log.e(TAG, "API call failed or response is null. Response code: " + response.code());
                    showError("Không thể tải danh sách chuyên khoa");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SpecialtyResponse>>> call, Throwable t) {
                Log.e(TAG, "Specialties by hospital API call failed", t);
                showLoading(false);
                showError("Lỗi kết nối mạng");
            }
        });
    }
    
    private void checkSpecialtyInHospital(SpecialtyResponse specialty, Integer hospitalId, List<Specialty> specialties) {
        // Load doctors for this specialty and check if any are in the hospital
        doctorApiService.getDoctorsBySpecialty(specialty.getSpecialtyId(), 1, 50).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Parse response to check if any doctors are in this hospital
                    // For now, we'll add the specialty if the API call succeeds
                    // In a real implementation, you'd parse the response and check hospital IDs
                    specialties.add(new Specialty(
                            specialty.getSpecialtyId(),
                            specialty.getName(),
                            specialty.getDescription(),
                            specialty.getDoctorCount()
                    ));
                    
                    // Update UI if this is the last specialty being checked
                    if (specialties.size() == 1) { // First specialty found
                        updateSpecialtiesList(specialties);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Failed to check doctors for specialty: " + specialty.getName(), t);
            }
        });
    }
    
    private void showNoSpecialtiesMessage() {
        String hospitalName = "";
        if (getActivity() instanceof BookingWizardActivity) {
            hospitalName = ((BookingWizardActivity) getActivity()).getHospitalName();
        }
        
        String message = "Bệnh viện " + hospitalName + " chưa có chuyên khoa hoặc bác sĩ khả dụng.";
        showError(message);
    }

    private void updateSpecialtiesList(List<Specialty> specialties) {
        if (specialties.isEmpty()) {
            showError("Không có chuyên khoa khả dụng");
        } else {
            specialtiesList.clear();
            specialtiesList.addAll(specialties);
            specialtyAdapter.updateSpecialties(specialtiesList);
            rvSpecialties.setVisibility(View.VISIBLE);
            tvNoSpecialties.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSpecialtySelected(Specialty specialty, int position) {
        Log.d(TAG, "Specialty selected: " + specialty.getName() + " (ID: " + specialty.getSpecialtyId() + ")");
        Toast.makeText(requireContext(), "Đã chọn: " + specialty.getName(), Toast.LENGTH_SHORT).show();

        bookingData.specialtyId = specialty.getSpecialtyId();
        bookingData.specialtyName = specialty.getName();
        
        Log.d(TAG, "Updated bookingData - specialtyId: " + bookingData.specialtyId + ", specialtyName: " + bookingData.specialtyName);

        if (getActivity() instanceof BookingWizardActivity) {
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvSpecialties.setVisibility(show ? View.GONE : View.VISIBLE);
        tvNoSpecialties.setVisibility(View.GONE);
    }

    private void showSelectedSpecialtyInfo() {
        // Hide the specialties list
        rvSpecialties.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        
        // Show selected specialty info
        tvNoSpecialties.setVisibility(View.VISIBLE);
        tvNoSpecialties.setText("Chuyên khoa đã chọn: " + bookingData.specialtyName + 
                               "\nMô tả: " + (bookingData.specialtyName != null ? bookingData.specialtyName : "Không có mô tả"));
        
        // Notify parent that this step is completed
        if (getActivity() instanceof BookingWizardActivity) {
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        }
    }
    
    private void showError(String message) {
        rvSpecialties.setVisibility(View.GONE);
        tvNoSpecialties.setVisibility(View.VISIBLE);
        tvNoSpecialties.setText(message);
    }
}
