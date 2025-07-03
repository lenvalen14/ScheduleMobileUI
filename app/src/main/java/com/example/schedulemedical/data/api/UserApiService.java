package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.response.ApiResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {
    
    // Get user count
    @GET("users/count")
    Call<ApiResponse<Object>> getUserCount();
    
    // Get all users with pagination
    @GET("users")
    Call<ApiResponse<Object>> getAllUsers(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    // Get user by ID
    @GET("users/{id}")
    Call<ApiResponse<Object>> getUserById(@Path("id") int userId);
    
    // Update user
    @PUT("users/{id}")
    Call<ApiResponse<Object>> updateUser(
        @Path("id") int userId,
        @Body Object updateUserRequest
    );
    
    // Delete user
    @DELETE("users/{id}")
    Call<ApiResponse<Object>> deleteUser(@Path("id") int userId);
    
    // Upload avatar
    @Multipart
    @POST("users/avatar/{userId}")
    Call<ApiResponse<Object>> uploadAvatar(
        @Path("userId") int userId,
        @Part MultipartBody.Part file
    );
} 