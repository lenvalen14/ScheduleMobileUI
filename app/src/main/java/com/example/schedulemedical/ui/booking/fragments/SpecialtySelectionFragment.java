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
import com.example.schedulemedical.ui.booking.BookingWizardActivity;
import com.google.gson.Gson;

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
        loadSpecialties();
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
        
        doctorApiService.getAllSpecialties(1, 50).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<Object> apiResponse = response.body();
                        
                        if (apiResponse.getData() instanceof List) {
                            List<?> dataList = (List<?>) apiResponse.getData();
                            List<Specialty> specialties = new ArrayList<>();
                            
                            Gson gson = new Gson();
                            for (Object item : dataList) {
                                Specialty specialty = gson.fromJson(gson.toJson(item), Specialty.class);
                                specialties.add(specialty);
                            }
                            
                            updateSpecialtiesList(specialties);
                        } else {
                            showError("Không thể tải danh sách chuyên khoa");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing specialties", e);
                        showError("Lỗi khi xử lý dữ liệu chuyên khoa");
                    }
                } else {
                    showError("Không thể tải danh sách chuyên khoa");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Specialties API call failed", t);
                showLoading(false);
                showError("Lỗi kết nối mạng");
            }
        });
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
        // Add Toast to confirm selection is working
        Toast.makeText(requireContext(), "Đã chọn: " + specialty.getName(), Toast.LENGTH_SHORT).show();
        
        Log.d(TAG, "Specialty selected: " + specialty.getName());
        Log.d(TAG, "Specialty ID: " + specialty.getSpecialtyId());
        
        // Update booking data
        bookingData.specialtyId = specialty.getSpecialtyId();
        bookingData.specialtyName = specialty.getName();
        
        Log.d(TAG, "BookingData specialtyId after update: " + bookingData.specialtyId);
        
        // Notify parent activity
        if (getActivity() instanceof BookingWizardActivity) {
            Log.d(TAG, "Calling onStepDataChanged()");
            ((BookingWizardActivity) getActivity()).onStepDataChanged();
        } else {
            Log.e(TAG, "Activity is not BookingWizardActivity");
        }
    }
    
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvSpecialties.setVisibility(show ? View.GONE : View.VISIBLE);
        tvNoSpecialties.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        rvSpecialties.setVisibility(View.GONE);
        tvNoSpecialties.setVisibility(View.VISIBLE);
        tvNoSpecialties.setText(message);
    }
} 