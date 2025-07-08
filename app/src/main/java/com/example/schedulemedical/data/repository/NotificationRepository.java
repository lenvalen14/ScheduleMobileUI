package com.example.schedulemedical.data.repository;

import com.example.schedulemedical.data.api.NotificationApiService;
import com.example.schedulemedical.model.dto.response.NotificationResponse;

import retrofit2.Call;
import retrofit2.Retrofit;

public class NotificationRepository {
    private final NotificationApiService apiService;

    public NotificationRepository(Retrofit retrofit) {
        this.apiService = retrofit.create(NotificationApiService.class);
    }

    public Call<NotificationResponse> getNotificationsByUserId(int userId, int page, int limit) {
        return apiService.getNotificationsByUserId(userId, page, limit);
    }
} 