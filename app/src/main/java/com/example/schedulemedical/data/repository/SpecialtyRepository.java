package com.example.schedulemedical.data.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpecialtyRepository {
    public void getSpecialties(int page, int limit, Callback<com.example.schedulemedical.model.dto.response.ApiResponse<List<com.example.schedulemedical.model.dto.response.SpecialtyResponse>>> callback) {
        ApiClient.getDoctorApiService().getAllSpecialties(page, limit)
            .enqueue(callback);
    }
} 