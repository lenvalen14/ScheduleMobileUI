package com.example.schedulemedical.ui.filterDoctor;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;

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
        doctorRepository.filterDoctors(specialtyId, minRating, hospitalId, page, limit, _filteredDoctors);
    }
}
