package com.example.schedulemedical.ui.filterDoctor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;

import java.util.List;

public class DoctorFilterOptionsViewModel extends ViewModel {

    private final DoctorRepository repository;

    private final MutableLiveData<List<HospitalResponse>> _hospitals = new MutableLiveData<>();
    public LiveData<List<HospitalResponse>> hospitals = _hospitals;

    private final MutableLiveData<List<SpecialtyResponse>> _specialties = new MutableLiveData<>();
    public LiveData<List<SpecialtyResponse>> specialties = _specialties;

    public DoctorFilterOptionsViewModel(DoctorRepository repository) {
        this.repository = repository;
        loadHospitals();
        loadSpecialties();
    }

    public void loadHospitals() {
        repository.getAllHospitals(1, 100).observeForever(_hospitals::setValue);
    }

    public void loadSpecialties() {
        repository.getAllSpecialties(1, 100).observeForever(_specialties::setValue);
    }
}

