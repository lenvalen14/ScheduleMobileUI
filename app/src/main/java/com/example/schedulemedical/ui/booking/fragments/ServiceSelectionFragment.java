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

import com.example.schedulemedical.Adapter.ServiceAdapter;
import com.example.schedulemedical.R;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.ServiceApiService;
import com.example.schedulemedical.model.Service;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.ui.booking.BookingWizardActivity;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceSelectionFragment extends Fragment implements ServiceAdapter.OnServiceSelectedListener {
    private static final String TAG = "ServiceSelectionFragment";
    private static final String ARG_BOOKING_DATA = "booking_data";
    
    // UI Components
    private TextView tvBookingSummary;
    private RecyclerView rvServices;
    private ProgressBar progressBar;
    private TextView tvNoServices;
    
    // Data
    private BookingWizardActivity.BookingData bookingData;
    private List<Service> servicesList;
    private ServiceAdapter serviceAdapter;
    
    // Services
    private ServiceApiService serviceApiService;
    
    public static ServiceSelectionFragment newInstance(BookingWizardActivity.BookingData bookingData) {
        ServiceSelectionFragment fragment = new ServiceSelectionFragment();
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
        serviceApiService = ApiClient.getServiceApiService();
        servicesList = new ArrayList<>();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service_selection, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupRecyclerView();
        updateBookingSummary();
        loadServices();
    }
    
    private void initializeViews(View view) {
        tvBookingSummary = view.findViewById(R.id.tvBookingSummary);
        rvServices = view.findViewById(R.id.rvServices);
        progressBar = view.findViewById(R.id.progressBar);
        tvNoServices = view.findViewById(R.id.tvNoServices);
    }
    
    private void setupRecyclerView() {
        serviceAdapter = new ServiceAdapter(requireContext(), servicesList);
        serviceAdapter.setOnServiceSelectedListener(this);
        rvServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvServices.setAdapter(serviceAdapter);
    }
    
    private void updateBookingSummary() {
        StringBuilder summary = new StringBuilder();
        
        if (bookingData.doctorName != null) {
            summary.append("Bác sĩ: ").append(bookingData.doctorName).append("\n");
        }
        
        if (bookingData.selectedDate != null && bookingData.selectedTimeSlot != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            summary.append("Ngày khám: ").append(dateFormat.format(bookingData.selectedDate))
                   .append(" - ").append(bookingData.selectedTimeSlot);
        }
        
        tvBookingSummary.setText(summary.toString());
    }
    
    private void loadServices() {
        showLoading(true);
        
        serviceApiService.getServices(1, 50, null).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        ApiResponse<Object> apiResponse = response.body();
                        
                        if (apiResponse.getData() instanceof List) {
                            List<?> dataList = (List<?>) apiResponse.getData();
                            List<Service> services = new ArrayList<>();
                            
                            Gson gson = new Gson();
                            for (Object item : dataList) {
                                Service service = gson.fromJson(gson.toJson(item), Service.class);
                                services.add(service);
                            }
                            
                            updateServicesList(services);
                        } else {
                            showError("Không thể tải danh sách dịch vụ");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing services", e);
                        showError("Lỗi khi xử lý dữ liệu dịch vụ");
                    }
                } else {
                    showError("Không thể tải danh sách dịch vụ");
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                Log.e(TAG, "Services API call failed", t);
                showLoading(false);
                showError("Lỗi kết nối mạng");
            }
        });
    }
    
    private void updateServicesList(List<Service> services) {
        if (services.isEmpty()) {
            showError("Không có dịch vụ khả dụng");
        } else {
            servicesList.clear();
            servicesList.addAll(services);
            serviceAdapter.updateServices(servicesList);
            rvServices.setVisibility(View.VISIBLE);
            tvNoServices.setVisibility(View.GONE);
        }
    }
    
    @Override
    public void onServiceSelected(Service service, int position) {
        // Add Toast to confirm selection is working
        Toast.makeText(requireContext(), "Đã chọn: " + service.getName(), Toast.LENGTH_SHORT).show();
        
        Log.d(TAG, "Service selected: " + service.getName());
        Log.d(TAG, "Service ID: " + service.getServiceId());
        Log.d(TAG, "Service Price: " + service.getPrice());
        
        // Update booking data
        bookingData.serviceId = service.getServiceId();
        bookingData.serviceName = service.getServiceName();
        bookingData.servicePrice = service.getPrice();
        
        Log.d(TAG, "BookingData serviceId after update: " + bookingData.serviceId);
        
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
        rvServices.setVisibility(show ? View.GONE : View.VISIBLE);
        tvNoServices.setVisibility(View.GONE);
    }
    
    private void showError(String message) {
        rvServices.setVisibility(View.GONE);
        tvNoServices.setVisibility(View.VISIBLE);
        tvNoServices.setText(message);
    }
} 