package com.example.schedulemedical.data.repository;

import android.util.Log;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AppointmentApiService;
import com.example.schedulemedical.model.dto.response.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentRepository {
    private static final String TAG = "AppointmentRepository";
    private final AppointmentApiService apiService;

    public AppointmentRepository() {
        this.apiService = ApiClient.getAppointmentApiService();
    }

    public void getAppointments(int userId, String status, int page, int limit, final DataCallback<ApiResponse> callback) {
        apiService.getAppointments(page, limit, userId, null, status)
            .enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        callback.onError("No data or error response");
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.e(TAG, "API call failed", t);
                    callback.onError(t.getMessage());
                }
            });
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
} 