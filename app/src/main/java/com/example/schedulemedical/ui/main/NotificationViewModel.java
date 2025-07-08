package com.example.schedulemedical.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.NotificationRepository;
import com.example.schedulemedical.model.dto.response.NotificationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NotificationViewModel extends ViewModel {
    private final NotificationRepository repository;
    private final MutableLiveData<NotificationResponse> notifications = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public NotificationViewModel(Retrofit retrofit) {
        this.repository = new NotificationRepository(retrofit);
    }

    public LiveData<NotificationResponse> getNotifications() {
        return notifications;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchNotifications(int userId, int page, int limit) {
        isLoading.setValue(true);
        repository.getNotificationsByUserId(userId, page, limit).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    notifications.setValue(response.body());
                } else {
                    error.setValue("Lỗi lấy danh sách thông báo");
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue(t.getMessage());
            }
        });
    }
} 