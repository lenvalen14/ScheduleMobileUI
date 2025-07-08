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
            Callback<DoctorListResponse> callback
    ) {
        Log.d(TAG, "Filtering doctors with: specialtyId=" + specialtyId + ", minRating=" + minRating +
                ", hospitalId=" + hospitalId + ", page=" + page + ", limit=" + limit);

        ApiClient.getDoctorApiService().filterDoctors(specialtyId, minRating, hospitalId, page, limit)
                .enqueue(callback);
    }

    public void getDoctorProfileByUserId(int userId, MutableLiveData<DoctorResponse> result) {
        ApiClient.getDoctorApiService().getDoctorByUserId(userId)
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

}
