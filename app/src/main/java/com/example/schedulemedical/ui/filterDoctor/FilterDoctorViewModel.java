package com.example.schedulemedical.ui.filterDoctor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;

import java.util.List;

public class FilterDoctorViewModel extends ViewModel {

    private final DoctorRepository doctorRepository = new DoctorRepository();

    private final MutableLiveData<ResponseWrapper<List<DoctorResponse>>> _filteredDoctors = new MutableLiveData<>();
    public LiveData<ResponseWrapper<List<DoctorResponse>>> filteredDoctors = _filteredDoctors;

    public void filterDoctors(Integer specialtyId, Float minRating, Integer hospitalId, Integer page, Integer limit) {
        doctorRepository.filterDoctors(specialtyId, minRating, hospitalId, page, limit, _filteredDoctors);
    }
}
