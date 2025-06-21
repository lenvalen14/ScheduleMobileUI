package com.example.schedulemedical.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.dto.request.AccountLoginRequest;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.TokenDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    public void login(AccountLoginRequest request, MutableLiveData<ResponseWrapper<TokenDTO>> loginResult) {
        ApiClient.getApiService().login(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<TokenDTO>> call, @NonNull Response<ResponseWrapper<TokenDTO>> response) {
                loginResult.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<TokenDTO>> call, @NonNull Throwable t) {
                ResponseWrapper<TokenDTO> error = new ResponseWrapper<>("Network error", null);
                loginResult.setValue(error);
            }
        });
    }
}