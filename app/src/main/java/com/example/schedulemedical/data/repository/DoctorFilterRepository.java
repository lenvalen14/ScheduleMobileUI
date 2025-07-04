package com.example.schedulemedical.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.DoctorApiService;
import com.example.schedulemedical.data.api.HospitalApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.HospitalListResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorFilterRepository {

    private final DoctorApiService doctorApiService;
    private final HospitalApiService hospitalApiService;

    public DoctorFilterRepository(DoctorApiService doctorApiService, HospitalApiService hospitalApiService) {
        this.doctorApiService = doctorApiService;
        this.hospitalApiService = hospitalApiService;
    }

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
