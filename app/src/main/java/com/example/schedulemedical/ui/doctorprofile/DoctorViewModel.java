package com.example.schedulemedical.ui.doctorprofile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.data.repository.LoginRepository;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;

import java.util.List;

public class DoctorViewModel extends ViewModel {
    private final DoctorRepository repository = new DoctorRepository();
    public MutableLiveData<ResponseWrapper<List<CertificationResponseDTO>>> certifications = new MutableLiveData<>();

    public void loadDoctorCertifications(int doctorId, int page, int limit) {
        repository.getDoctorCertifications(doctorId, page, limit, certifications);
    }
} 