package com.example.schedulemedical.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
import com.example.schedulemedical.data.repository.HospitalRepository;
import com.example.schedulemedical.data.repository.SpecialtyRepository;
import com.example.schedulemedical.model.dto.response.ApiResponse;
import com.example.schedulemedical.model.dto.response.DoctorListResponse;
import com.example.schedulemedical.model.dto.response.DoctorResponse;
import com.example.schedulemedical.model.dto.response.HospitalListResponse;
import com.example.schedulemedical.model.dto.response.HospitalResponse;
import com.example.schedulemedical.model.dto.response.SpecialtyResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {

    private final DoctorRepository doctorRepository = new DoctorRepository();
    private final HospitalRepository hospitalRepository = new HospitalRepository();
    private final SpecialtyRepository specialtyRepository = new SpecialtyRepository();

    // Dùng MutableLiveData để có thể thay đổi giá trị
    private final MutableLiveData<List<DoctorResponse>> _doctors = new MutableLiveData<>();
    private final MutableLiveData<List<HospitalResponse>> _hospitals = new MutableLiveData<>();
    private final MutableLiveData<List<SpecialtyResponse>> _specialties = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();

    // Cung cấp LiveData không thể thay đổi cho Activity
    public LiveData<List<DoctorResponse>> getDoctors() { return _doctors; }
    public LiveData<List<HospitalResponse>> getHospitals() { return _hospitals; }
    public LiveData<List<SpecialtyResponse>> getSpecialties() { return _specialties; }
    public LiveData<Boolean> isLoading() { return _isLoading; }
    public LiveData<String> getErrorMessage() { return _errorMessage; }

    public void loadAllDashboardData() {
        _isLoading.setValue(true);
        AtomicInteger apiCallCounter = new AtomicInteger(3); // Đếm 3 lệnh gọi API

        Runnable checkCompletion = () -> {
            if (apiCallCounter.decrementAndGet() == 0) {
                _isLoading.postValue(false); // Chỉ ẩn loading khi tất cả đã xong
            }
        };

        doctorRepository.filterDoctorsRaw(null, null, null, 1, 10, new Callback<DoctorListResponse>() {
            @Override
            public void onResponse(Call<DoctorListResponse> call, Response<DoctorListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _doctors.postValue(response.body().getData());
                } else {
                    _errorMessage.postValue("Lỗi tải danh sách bác sĩ");
                }
                checkCompletion.run();
            }

            @Override
            public void onFailure(Call<DoctorListResponse> call, Throwable t) {
                _errorMessage.postValue("Lỗi mạng: " + t.getMessage());
                checkCompletion.run();
            }
        });

        hospitalRepository.getHospitals(1, 10, new Callback<HospitalListResponse>() {
            @Override
            public void onResponse(Call<HospitalListResponse> call, Response<HospitalListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _hospitals.postValue(response.body().getData());
                } else {
                    _errorMessage.postValue("Lỗi tải danh sách bệnh viện");
                }
                checkCompletion.run();
            }

            @Override
            public void onFailure(Call<HospitalListResponse> call, Throwable t) {
                _errorMessage.postValue("Lỗi mạng: " + t.getMessage());
                checkCompletion.run();
            }
        });

        specialtyRepository.getSpecialties(1, 10, new Callback<ApiResponse<List<SpecialtyResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<SpecialtyResponse>>> call, Response<ApiResponse<List<SpecialtyResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _specialties.postValue(response.body().getData());
                } else {
                    _errorMessage.postValue("Lỗi tải danh sách chuyên khoa");
                }
                checkCompletion.run();
            }

            @Override
            public void onFailure(Call<ApiResponse<List<SpecialtyResponse>>> call, Throwable t) {
                _errorMessage.postValue("Lỗi mạng: " + t.getMessage());
                checkCompletion.run();
            }
        });
    }
}