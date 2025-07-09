package com.example.schedulemedical.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.UserRepository;
import com.example.schedulemedical.model.dto.request.UpdatePatientProfileRequest;
import com.example.schedulemedical.model.dto.request.UpdateUserRequest;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.PatientProfileResponse;

import okhttp3.MultipartBody;

public class ProfileViewModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();

    public LiveData<ApiResponse<Object>> updateUser(int userId, UpdateUserRequest request) {
        return userRepository.updateUser(userId, request);
    }

    public LiveData<ApiResponse<PatientProfileResponse>> updatePatientProfile(int userId, UpdatePatientProfileRequest request) {
        return userRepository.updatePatientProfile(userId, request);
    }

    public LiveData<ApiResponse<Object>> uploadAvatar(int userId, MultipartBody.Part filePart) {
        return userRepository.uploadAvatar(userId, filePart);
    }
}
