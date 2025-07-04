package com.example.schedulemedical.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.DoctorResponseDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorRepository {

    public void getDoctorCertifications(int doctorId, int page, int limit, MutableLiveData<ResponseWrapper<List<CertificationResponseDTO>>> result) {
        ApiClient.getDoctorApiService().getDoctorCertifications(doctorId, page, limit)
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

    public void filterDoctors(
            Integer specialtyId,
            Float minRating,
            Integer hospitalId,
            Integer page,
            Integer limit,
            MutableLiveData<ResponseWrapper<List<DoctorResponse>>> result
    ) {
        ApiClient.getDoctorApiService().filterDoctors(specialtyId, minRating, hospitalId, page, limit)
                .enqueue(new Callback<ApiResponse<List<DoctorResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<DoctorResponse>>> call, @NonNull Response<ApiResponse<List<DoctorResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<DoctorResponse> doctors = response.body().getData();
                            result.postValue(new ResponseWrapper<>("Success", doctors));
                        } else {
                            result.postValue(new ResponseWrapper<>("Lỗi không xác định", null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<DoctorResponse>>> call, @NonNull Throwable t) {
                        result.postValue(new ResponseWrapper<>("Lỗi kết nối: " + t.getMessage(), null));
                    }
                });
    }
}
