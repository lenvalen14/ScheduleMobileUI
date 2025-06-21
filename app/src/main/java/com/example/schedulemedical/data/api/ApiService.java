package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.request.AccountLoginRequest;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.TokenDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("account/login")
    Call<ResponseWrapper<TokenDTO>> login(@Body AccountLoginRequest request);
}
