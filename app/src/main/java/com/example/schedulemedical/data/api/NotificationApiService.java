package com.example.schedulemedical.data.api;

import com.example.schedulemedical.model.dto.response.NotificationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationApiService {
    @GET("appointment/notification/user/{userId}")
    Call<NotificationResponse> getNotificationsByUserId(
        @Path("userId") int userId,
        @Query("page") int page,
        @Query("limit") int limit
    );
} 