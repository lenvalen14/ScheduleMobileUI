package com.example.schedulemedical.ui.filterDoctor;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.schedulemedical.data.repository.DoctorFilterRepository;

public class DoctorFilterOptionsViewModelFactory implements ViewModelProvider.Factory {

    private final DoctorFilterRepository repository;

    public DoctorFilterOptionsViewModelFactory(DoctorFilterRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DoctorFilterOptionsViewModel.class)) {
            return (T) new DoctorFilterOptionsViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
