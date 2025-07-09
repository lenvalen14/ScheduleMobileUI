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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger; // NOTE: Thêm import này

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

        Log.d("ham nay dc goi n1",hospitalId.toString() );
        if (hospitalId != null && hospitalId > 0) {
            // Load specialties for specific hospital
            loadSpecialtiesByHospital(hospitalId);
        } else {
            // Load all specialties
            Log.d("ham nay dc goi", "all specialties");
            loadAllSpecialties();
        }
    }

    private void loadAllSpecialties() {
        doctorApiService.getAllSpecialties(1, 50).enqueue(new Callback<ApiResponse<List<SpecialtyResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SpecialtyResponse> responseList = response.body().getData();
                    List<Specialty> specialties = new ArrayList<>();
                    for (SpecialtyResponse res : responseList) {
                        specialties.add(new Specialty(
                                res.getSpecialtyId(),
                                res.getName(),
                                res.getDescription(),
                                res.getDoctorCount()
                        ));
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

    // NOTE: Sửa lại phương thức này để xử lý logic bất đồng bộ
    private void loadSpecialtiesByHospital(Integer hospitalId) {
        doctorApiService.getSpecialtiesByHospitalNew(hospitalId, 1,50).enqueue(new Callback<ApiResponse<List<SpecialtyResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                showLoading(false);
                Log.d("ham nay dc goi n2 dang bat dau",hospitalId.toString() );
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(response.body());
                Log.d("API RESPONSE", json);
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<SpecialtyResponse> responseList = response.body().getData();
                    if (responseList.isEmpty()) {
                        showNoSpecialtiesMessage();
                        return;
                    }

                    List<Specialty> availableSpecialties = new ArrayList<>();
                    AtomicInteger pendingCalls = new AtomicInteger(responseList.size());

                    for (SpecialtyResponse res : responseList) {
                        checkSpecialtyInHospital(res, hospitalId, availableSpecialties, pendingCalls);
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

    private void checkSpecialtyInHospital(SpecialtyResponse specialty, Integer hospitalId, List<Specialty> availableSpecialties, AtomicInteger pendingCalls) {
//        Gson gson = new Gson();
//        String specialtyJson = gson.toJson(specialty);
//        String availableSpecialtiesJson = gson.toJson(availableSpecialties);
//
//        Log.d(TAG, "checkSpecialtyInHospital -> INPUT DATA:" +
//                "\n>> specialty (JSON): " + specialtyJson +
//                "\n>> hospitalId: " + hospitalId +
//                "\n>> availableSpecialties (JSON): " + availableSpecialtiesJson +
//                "\n>> pendingCalls (current value): " + pendingCalls.get()
//        );

        boolean hasDoctorsInHospital = false;
        if (specialty.getDoctorCount() != null) {

            hasDoctorsInHospital = true;
        }

        if (hasDoctorsInHospital) {
            availableSpecialties.add(new Specialty(
                    specialty.getSpecialtyId(),
                    specialty.getName(),
                    specialty.getDescription(),
                    specialty.getDoctorCount()
            ));
        }

        int remaining = pendingCalls.decrementAndGet();

        if (remaining == 0) {
            Log.d(TAG, "All checks completed. Total available specialties: " + availableSpecialties.size());
            Collections.sort(availableSpecialties, (s1, s2) -> s1.getName().compareTo(s2.getName()));
            updateSpecialtiesList(availableSpecialties);
        }
    }


    private void showNoSpecialtiesMessage() {
        String hospitalName = "";
        if (getActivity() instanceof BookingWizardActivity) {
            hospitalName = ((BookingWizardActivity) getActivity()).getHospitalName();
        }
        // NOTE: Cải thiện thông báo lỗi
        String message = "Bệnh viện " + (hospitalName != null ? hospitalName : "") + " hiện chưa có chuyên khoa nào để đặt lịch.";
        showError(message);
    }

    private void updateSpecialtiesList(List<Specialty> specialties) {
        if (specialties == null || specialties.isEmpty()) {
            showNoSpecialtiesMessage();
        } else {
            specialtiesList.clear();
            specialtiesList.addAll(specialties);
            specialtyAdapter.updateSpecialties(specialtiesList); // Giả sử adapter có phương thức này
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
        rvSpecialties.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        tvNoSpecialties.setVisibility(View.VISIBLE);
        tvNoSpecialties.setText("Chuyên khoa đã chọn: " + bookingData.specialtyName);

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