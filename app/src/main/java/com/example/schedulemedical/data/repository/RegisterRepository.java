package com.example.schedulemedical.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.dto.request.RegisterRequest;
import com.example.schedulemedical.model.dto.response.RegisterResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {

    public void register(RegisterRequest request, MutableLiveData<RegisterResponse> registerResult) {
        // Sử dụng cùng ApiClient như LoginRepository
        ApiClient.getAuthApiService().register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(@NonNull Call<RegisterResponse> call, @NonNull Response<RegisterResponse> response) {
                registerResult.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<RegisterResponse> call, @NonNull Throwable t) {
                // Tạo error response
                RegisterResponse error = new RegisterResponse();
                error.setSuccess(false);
                error.setMessage("Network error: " + t.getMessage());
                registerResult.setValue(error);
            }
        });
    }
}