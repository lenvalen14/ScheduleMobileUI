package com.example.schedulemedical.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.data.api.UserApiService;
import com.example.schedulemedical.model.dto.request.UpdatePatientProfileRequest;
import com.example.schedulemedical.model.dto.request.UpdateUserRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.PatientProfileResponse;


import java.io.IOException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {

    private final UserApiService userApiService;

    public UserRepository() {
        this.userApiService = ApiClient.getUserApiService();
    }

    public LiveData<ApiResponse<Object>> updateUser(int userId, UpdateUserRequest request) {
        MutableLiveData<ApiResponse<Object>> result = new MutableLiveData<>();

        userApiService.updateUser(userId, request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    ApiResponse<Object> errorResponse = new ApiResponse<>();
                    errorResponse.setCode(response.code());
                    try {
                        errorResponse.setMessage(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        errorResponse.setMessage("Error reading error body");
                    }
                    result.postValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                ApiResponse<Object> errorResponse = new ApiResponse<>();
                errorResponse.setCode(500);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                result.postValue(errorResponse);
            }
        });

        return result;
    }

    public LiveData<ApiResponse<PatientProfileResponse>> updatePatientProfile(int userId, UpdatePatientProfileRequest request) {
        MutableLiveData<ApiResponse<PatientProfileResponse>> result = new MutableLiveData<>();

        userApiService.updatePatientProfile(userId, request).enqueue(new Callback<ApiResponse<PatientProfileResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PatientProfileResponse>> call, Response<ApiResponse<PatientProfileResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    ApiResponse<PatientProfileResponse> errorResponse = new ApiResponse<>();
                    errorResponse.setCode(response.code());
                    try {
                        errorResponse.setMessage(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        errorResponse.setMessage("Error reading error body");
                    }
                    result.postValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PatientProfileResponse>> call, Throwable t) {
                ApiResponse<PatientProfileResponse> errorResponse = new ApiResponse<>();
                errorResponse.setCode(500);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                result.postValue(errorResponse);
            }
        });

        return result;
    }

    public LiveData<ApiResponse<Object>> uploadAvatar(int userId, MultipartBody.Part filePart) {
        MutableLiveData<ApiResponse<Object>> result = new MutableLiveData<>();

        userApiService.uploadAvatar(userId, filePart).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(response.body());
                } else {
                    ApiResponse<Object> errorResponse = new ApiResponse<>();
                    errorResponse.setCode(response.code());
                    try {
                        errorResponse.setMessage(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        errorResponse.setMessage("Error reading error body");
                    }
                    result.postValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                ApiResponse<Object> errorResponse = new ApiResponse<>();
                errorResponse.setCode(500);
                errorResponse.setMessage("Lỗi kết nối: " + t.getMessage());
                result.postValue(errorResponse);
            }
        });

        return result;
    }

}
