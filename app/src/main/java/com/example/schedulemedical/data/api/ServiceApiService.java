package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.response.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServiceApiService {
    
    // Get all services with pagination
    @GET("services")
    Call<ApiResponse<Object>> getAllServices(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get service by ID
    @GET("services/{id}")
    Call<ApiResponse<Object>> getServiceById(@Path("id") int serviceId);
} 