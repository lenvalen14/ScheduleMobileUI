package com.example.schedulemedical.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.data.api.HospitalApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.HospitalListResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.DoctorResponseDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorRepository {

    private static final String TAG = "DoctorRepository";
    private final DoctorApiService doctorApiService;
    private final HospitalApiService hospitalApiService;

    public DoctorRepository() {
        this.doctorApiService = ApiClient.getDoctorApiService();
        this.hospitalApiService = ApiClient.getHospitalApiService();
    }

    // === GET Doctor certifications ===
    public void getDoctorCertifications(int doctorId, int page, int limit, MutableLiveData<ResponseWrapper<List<CertificationResponseDTO>>> result) {
        doctorApiService.getDoctorCertifications(doctorId, page, limit)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseWrapper<List<CertificationResponseDTO>>> call, @NonNull Response<ResponseWrapper<List<CertificationResponseDTO>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            result.setValue(response.body());
                        } else {
                            result.setValue(new ResponseWrapper<>("Unexpected error", null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseWrapper<List<CertificationResponseDTO>>> call, @NonNull Throwable t) {
                        result.setValue(new ResponseWrapper<>("Network error", null));
                    }
                });
    }

    public void filterDoctorsRaw(
            Integer specialtyId,
            Float minRating,
            Integer hospitalId,
            Integer page,
            Integer limit,
            Callback<DoctorListResponse> callback
    ) {
        doctorApiService.filterDoctors(specialtyId, minRating, hospitalId, page, limit)
                .enqueue(callback);
    }

    // === Filter doctors ===
    public LiveData<ResponseWrapper<DoctorListResponse>> filterDoctors(
            Integer specialtyId,
            Float minRating,
            Integer hospitalId,
            Integer page,
            Integer limit
    ) {
        MutableLiveData<ResponseWrapper<DoctorListResponse>> filteredDoctorsLiveData = new MutableLiveData<>();

        doctorApiService.filterDoctors(specialtyId, minRating, hospitalId, page, limit)
                .enqueue(new Callback<DoctorListResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<DoctorListResponse> call, @NonNull Response<DoctorListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            filteredDoctorsLiveData.setValue(new ResponseWrapper<>("Success", response.body()));
                        } else {
                            filteredDoctorsLiveData.setValue(new ResponseWrapper<>("Error", null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DoctorListResponse> call, @NonNull Throwable t) {
                        filteredDoctorsLiveData.setValue(new ResponseWrapper<>("Network error", null));
                    }
                });

        return filteredDoctorsLiveData;
    }


    // === Get doctor profile by user ID ===
    public void getDoctorProfileByUserId(int userId, MutableLiveData<DoctorResponse> result) {
        doctorApiService.getDoctorByUserId(userId)
                .enqueue(new Callback<ApiResponse<DoctorResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<DoctorResponse>> call,
                                           @NonNull Response<ApiResponse<DoctorResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            result.setValue(response.body().getData());
                        } else {
                            result.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<DoctorResponse>> call, @NonNull Throwable t) {
                        result.setValue(null);
                    }
                });
    }

    // === Get all hospitals ===
    public LiveData<List<HospitalResponse>> getAllHospitals(int page, int limit) {
        MutableLiveData<List<HospitalResponse>> hospitalsLiveData = new MutableLiveData<>();
        hospitalApiService.getAllHospitals(page, limit, null, null, null, null, null, null, null)
                .enqueue(new Callback<HospitalListResponse>() {
                    @Override
                    public void onResponse(Call<HospitalListResponse> call, Response<HospitalListResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            hospitalsLiveData.setValue(response.body().getData());
                        } else {
                            hospitalsLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<HospitalListResponse> call, Throwable t) {
                        hospitalsLiveData.setValue(null);
                    }
                });
        return hospitalsLiveData;
    }

    // === Get all specialties ===
    public LiveData<List<SpecialtyResponse>> getAllSpecialties(int page, int limit) {
        MutableLiveData<List<SpecialtyResponse>> specialtiesLiveData = new MutableLiveData<>();
        doctorApiService.getAllSpecialties(page, limit)
                .enqueue(new Callback<ApiResponse<List<SpecialtyResponse>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            specialtiesLiveData.setValue(response.body().getData());
                        } else {
                            specialtiesLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<SpecialtyResponse>>> call, Throwable t) {
                        specialtiesLiveData.setValue(null);
                    }
                });
        return specialtiesLiveData;
    }

}
