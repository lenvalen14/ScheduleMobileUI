package com.example.schedulemedical.ui.doctorprofile;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;
import com.example.schedulemedical.model.dto.response.DoctorResponse;

import java.util.List;

public class DoctorViewModel extends ViewModel {
    private final DoctorRepository repository = new DoctorRepository();
    private final MutableLiveData<ResponseWrapper<DoctorListResponse>> _filteredDoctors = new MutableLiveData<>();

    public MutableLiveData<ResponseWrapper<DoctorListResponse>> filteredDoctors = _filteredDoctors;
    public MutableLiveData<ResponseWrapper<List<CertificationResponseDTO>>> certifications = new MutableLiveData<>();
    public MutableLiveData<DoctorResponse> doctorProfile = new MutableLiveData<>();

    public void loadDoctorCertifications(int doctorId, int page, int limit) {
        repository.getDoctorCertifications(doctorId, page, limit, certifications);
    }

    public void loadDoctorProfileByUserId(int userId) {
        repository.getDoctorProfileByUserId(userId, doctorProfile);
    }

    public void filterDoctors(
            @Nullable Integer specialtyId,
            @Nullable Float minRating,
            @Nullable Integer hospitalId,
            int page,
            int limit
    ) {
        repository.filterDoctors(specialtyId, minRating, hospitalId, page, limit)
                .observeForever(_filteredDoctors::postValue);
    }
}