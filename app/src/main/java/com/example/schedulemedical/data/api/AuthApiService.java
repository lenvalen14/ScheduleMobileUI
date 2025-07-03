package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.request.LoginRequest;
import com.example.schedulemedical.model.dto.request.RegisterRequest;
import com.example.schedulemedical.model.dto.request.RefreshTokenRequest;
import com.example.schedulemedical.model.dto.request.LogoutRequest;
import com.example.schedulemedical.model.dto.response.LoginResponse;
import com.example.schedulemedical.model.dto.response.RegisterResponse;
import com.example.schedulemedical.model.dto.response.RefreshTokenResponse;
import com.example.schedulemedical.model.dto.response.ProfileResponse;
import com.example.schedulemedical.model.dto.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {
    
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest registerRequest);
    
    @POST("auth/logout")
    Call<ApiResponse<String>> logout(@Body LogoutRequest logoutRequest);
    
    @POST("auth/refresh-token")
    Call<RefreshTokenResponse> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);
    
    @GET("auth/profile")
    Call<ProfileResponse> getProfile();
    
    @GET("auth/verify-email")
    Call<ApiResponse<String>> verifyEmail(@Query("token") String token);
    
    // Google Auth endpoints
    @GET("auth/google")
    Call<Void> googleLogin(); // This will redirect to Google OAuth
} 