package com.example.schedulemedical.data.repository;

import android.util.Log;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.AppointmentApiService;
import com.example.schedulemedical.model.dto.request.UpdateAppointmentStatusRequest;
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


    public void confirmAppointment(int appointmentId, final DataCallback<Boolean> callback) {
        UpdateAppointmentStatusRequest request = new UpdateAppointmentStatusRequest("SCHEDULED");

        Call<ApiResponse> call = apiService.updateAppointmentStatus(appointmentId, request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Lỗi xác nhận: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }

    public void cancelAppointment(int appointmentId, final DataCallback<Boolean> callback) {
        UpdateAppointmentStatusRequest request = new UpdateAppointmentStatusRequest("CANCELLED");

        Call<ApiResponse> call = apiService.updateAppointmentStatus(appointmentId, request);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(true);
                } else {
                    callback.onError("Lỗi huỷ lịch: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("Lỗi mạng: " + t.getMessage());
            }
        });
    }


    public void getAppointments(int page, int limit, Integer userId, Integer doctorId, String status,
                                final DataCallback<ApiResponse> callback) {

        apiService.getAppointments(page, limit, userId, doctorId, status).enqueue(new Callback<ApiResponse>() {
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

//    public void cancelAppointment(int appointmentId, DataCallback<Boolean> callback) {
//        apiService.deleteAppointment(appointmentId).enqueue(new Callback<ApiResponse<Object>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
//                if (response.isSuccessful()) {
//                    callback.onSuccess(true);
//                } else {
//                    callback.onError("Cancel failed");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
//                callback.onError(t.getMessage());
//            }
//        });
//    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}
