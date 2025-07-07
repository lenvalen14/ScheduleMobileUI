package com.example.schedulemedical.ui.filterDoctor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterDoctorViewModel extends ViewModel {

    private final DoctorRepository doctorRepository = new DoctorRepository();

    private final MutableLiveData<ResponseWrapper<DoctorListResponse>> _filteredDoctors = new MutableLiveData<>();
    public LiveData<ResponseWrapper<DoctorListResponse>> filteredDoctors = _filteredDoctors;

    public void filterDoctors(
            @Nullable Integer specialtyId,
            @Nullable Float minRating,
            @Nullable Integer hospitalId,
            int page,
            int limit
    ) {
        doctorRepository.filterDoctors(specialtyId, minRating, hospitalId, page, limit, new Callback<DoctorListResponse>() {
            @Override
            public void onResponse(Call<DoctorListResponse> call, Response<DoctorListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _filteredDoctors.postValue(new ResponseWrapper<>("Success", response.body()));
                } else {
                    _filteredDoctors.postValue(new ResponseWrapper<>("Error", null));
                }
            }
            @Override
            public void onFailure(Call<DoctorListResponse> call, Throwable t) {
                _filteredDoctors.postValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }
}
