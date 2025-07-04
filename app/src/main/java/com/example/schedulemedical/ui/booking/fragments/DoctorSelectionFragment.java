package com.example.schedulemedical.ui.booking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulemedical.Adapter.DoctorSelectionAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.model.Doctor;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.ui.booking.BookingWizardActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorSelectionFragment extends Fragment implements DoctorSelectionAdapter.OnDoctorSelectedListener {
    private static final String TAG = "DoctorSelectionFragment";
    private static final String ARG_BOOKING_DATA = "booking_data";
    
    // UI Components
    private RecyclerView rvDoctors;
    private ProgressBar progressBar;
    private TextView tvNoDoctors, tvSelectedSpecialty;
    
    // Data
    private BookingWizardActivity.BookingData bookingData;
    private List<Doctor> doctorsList;
    private DoctorSelectionAdapter doctorAdapter;
    
    // Services
    private DoctorApiService doctorApiService;
    
    public static DoctorSelectionFragment newInstance(BookingWizardActivity.BookingData bookingData) {
        DoctorSelectionFragment fragment = new DoctorSelectionFragment();
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
        doctorsList = new ArrayList<>();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_doctor_selection, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupRecyclerView();
        updateSpecialtyInfo();
        loadDoctors();
    }
    
    private void initializeViews(View view) {
        rvDoctors = view.findViewById(R.id.rvDoctors);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoDoctors = view.findViewById(R.id.tvNoDoctors);
        tvSelectedSpecialty = view.findViewById(R.id.tvSelectedSpecialty);
    }
    
    private void setupRecyclerView() {
        doctorAdapter = new DoctorSelectionAdapter(requireContext(), doctorsList);
        doctorAdapter.setOnDoctorSelectedListener(this);
        rvDoctors.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDoctors.setAdapter(doctorAdapter);
    }
    
    private void updateSpecialtyInfo() {
        if (bookingData.specialtyName != null) {
            tvSelectedSpecialty.setText("Chuyên khoa: " + bookingData.specialtyName);
            tvSelectedSpecialty.setVisibility(View.VISIBLE);
        } else {
            tvSelectedSpecialty.setVisibility(View.GONE);
        }
    }
    
    private void loadDoctors() {
        if (bookingData.specialtyId == null) {
            showError("Vui lòng chọn chuyên khoa trước");
            return;
        }
        
        showLoading(true);
        
        doctorApiService.getDoctorsBySpecialty(bookingData.specialtyId, 1, 50).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<Object> apiResponse = response.body();
                        
                        if (apiResponse.getData() instanceof List) {
                            List<?> dataList = (List<?>) apiResponse.getData();
                            List<Doctor> doctors = new ArrayList<>();
                            
                            Gson gson = new Gson();
                            for (Object item : dataList) {
                                Doctor doctor = gson.fromJson(gson.toJson(item), Doctor.class);
                                doctors.add(doctor);
                            }
                            
                            updateDoctorsList(doctors);
                        } else {
                            showError("Không thể tải danh sách bác sĩ");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing doctors", e);
                        showError("Lỗi khi xử lý dữ liệu bác sĩ");
                    }
                } else {
                    showError("Không thể tải danh sách bác sĩ");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Doctors API call failed", t);
                showLoading(false);
                showError("Lỗi kết nối mạng");
            }
        });
    }
    
    private void updateDoctorsList(List<Doctor> doctors) {
        if (doctors.isEmpty()) {
            showError("Không có bác sĩ khả dụng cho chuyên khoa này");
        } else {
            doctorsList.clear();
            doctorsList.addAll(doctors);
            doctorAdapter.updateDoctors(doctorsList);
            rvDoctors.setVisibility(View.VISIBLE);
            tvNoDoctors.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onDoctorSelected(Doctor doctor, int position) {
        // Update booking data
        bookingData.doctorId = doctor.getDoctorId();
        bookingData.doctorName = doctor.getFullName();
        bookingData.doctorSpecialty = doctor.getSpecialtyName();
        bookingData.hospitalName = doctor.getHospitalName();
        
        // Notify parent activity
        if (getActivity() instanceof BookingWizardActivity) {
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        }
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvDoctors.setVisibility(show ? View.GONE : View.VISIBLE);
        tvNoDoctors.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        rvDoctors.setVisibility(View.GONE);
        tvNoDoctors.setVisibility(View.VISIBLE);
        tvNoDoctors.setText(message);
    }
} 