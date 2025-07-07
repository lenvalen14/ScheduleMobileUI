package com.example.schedulemedical.data.repository;

import androidx.lifecycle.MutableLiveData;
import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HospitalRepository {
    public void getHospitals(int page, int limit, Callback<com.example.schedulemedical.model.dto.response.HospitalListResponse> callback) {
        ApiClient.getHospitalApiService().getAllHospitals(page, limit, null, null, null, null, null, null, null)
            .enqueue(callback);
    }
} 