package com.example.schedulemedical.data.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
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

    private static final String TAG = "DoctorRepository";

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
            MutableLiveData<ResponseWrapper<DoctorListResponse>> result
    ) {
        Log.d(TAG, "Filtering doctors with: specialtyId=" + specialtyId + ", minRating=" + minRating +
                ", hospitalId=" + hospitalId + ", page=" + page + ", limit=" + limit);

        ApiClient.getDoctorApiService().filterDoctors(specialtyId, minRating, hospitalId, page, limit)
                .enqueue(new Callback<DoctorListResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<DoctorListResponse> call, @NonNull Response<DoctorListResponse> response) {
                        Log.d(TAG, "API Response received: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            DoctorListResponse doctorListResponse = response.body();

                            List<DoctorResponse> doctorList = doctorListResponse.getData();

                            Log.d(TAG, "API Response body: " + new Gson().toJson(doctorListResponse));
                            Log.d(TAG, "Number of doctors received: " + (doctorList != null ? doctorList.size() : 0));

                            if (doctorList != null && !doctorList.isEmpty()) {
                                DoctorResponse firstDoctor = doctorList.get(0);
                                Log.d(TAG, "First doctor data: " + new Gson().toJson(firstDoctor));
                            }

                            result.postValue(new ResponseWrapper<>("Success", doctorListResponse));
                        } else {
                            Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                            try {
                                if (response.errorBody() != null) {
                                    Log.e(TAG, "Error body: " + response.errorBody().string());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Could not read error body", e);
                            }
                            result.postValue(new ResponseWrapper<>("Lỗi không xác định: " + response.code(), null));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<DoctorListResponse> call, @NonNull Throwable t) {
                        Log.e(TAG, "Network error: " + t.getMessage(), t);
                        result.postValue(new ResponseWrapper<>("Lỗi kết nối: " + t.getMessage(), null));
                    }
                });
    }

    public void getDoctorProfileByUserId(int userId, MutableLiveData<DoctorResponse> result) {
        ApiClient.getDoctorApiService().getDoctorsByUserIds(String.valueOf(userId), 1, 1)
                .enqueue(new Callback<ApiResponse<List<DoctorResponse>>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<List<DoctorResponse>>> call, @NonNull Response<ApiResponse<List<DoctorResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null && !response.body().getData().isEmpty()) {
                            result.setValue(response.body().getData().get(0));
                        } else {
                            result.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<List<DoctorResponse>>> call, @NonNull Throwable t) {
                        result.setValue(null);
                    }
                });
    }
}
