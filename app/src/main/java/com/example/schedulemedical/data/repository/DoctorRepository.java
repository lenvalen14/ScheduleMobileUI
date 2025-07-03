package com.example.schedulemedical.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.schedulemedical.data.api.ApiClient;
import com.example.schedulemedical.model.dto.request.doctor.AchievementDTO;
import com.example.schedulemedical.model.dto.request.doctor.CreateDoctorDTO;
import com.example.schedulemedical.model.dto.request.doctor.DoctorScheduleDTO;
import com.example.schedulemedical.model.dto.request.doctor.SpecialtyDTO;
import com.example.schedulemedical.model.dto.request.doctor.UpdateDoctorDTO;
import com.example.schedulemedical.model.dto.response.ResponseWrapper;
import com.example.schedulemedical.model.dto.response.doctor.AchievementResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.DoctorResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.DoctorScheduleResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.SpecialtyResponseDTO;
import com.example.schedulemedical.model.dto.response.doctor.CertificationResponseDTO;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorRepository {

    public void getTopRatedDoctors(MutableLiveData<List<DoctorResponseDTO>> result) {
        ApiClient.getDoctorApi().getTopRatedDoctors().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<DoctorResponseDTO>> call, @NonNull Response<List<DoctorResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<DoctorResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ArrayList<>());
            }
        });
    }

    public void getDoctorsBySpecialty(int specialtyId, int page, int limit, MutableLiveData<ResponseWrapper<List<DoctorResponseDTO>>> result) {
        ApiClient.getDoctorApi().getDoctorsBySpecialty(specialtyId, page, limit).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<List<DoctorResponseDTO>>> call, @NonNull Response<ResponseWrapper<List<DoctorResponseDTO>>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<List<DoctorResponseDTO>>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void getDoctorByUserId(int userId, MutableLiveData<ResponseWrapper<DoctorResponseDTO>> result) {
        ApiClient.getDoctorApi().getDoctorByUserId(userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<DoctorResponseDTO>> call, @NonNull Response<ResponseWrapper<DoctorResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<DoctorResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void createDoctor(CreateDoctorDTO doctor, MutableLiveData<ResponseWrapper<DoctorResponseDTO>> result) {
        ApiClient.getDoctorApi().createDoctor(doctor).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<DoctorResponseDTO>> call, @NonNull Response<ResponseWrapper<DoctorResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<DoctorResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void updateDoctor(int id, UpdateDoctorDTO doctor, MutableLiveData<ResponseWrapper<DoctorResponseDTO>> result) {
        ApiClient.getDoctorApi().updateDoctor(id, doctor).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<DoctorResponseDTO>> call, @NonNull Response<ResponseWrapper<DoctorResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<DoctorResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void updateDoctorRating(int id, float rating, MutableLiveData<ResponseWrapper<Void>> result) {
        RequestBody ratingBody = RequestBody.create(String.valueOf(rating), MediaType.parse("text/plain"));
        ApiClient.getDoctorApi().updateDoctorRating(id, ratingBody).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Response<ResponseWrapper<Void>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void createDoctorSchedule(DoctorScheduleDTO schedule, MutableLiveData<ResponseWrapper<DoctorScheduleResponseDTO>> result) {
        ApiClient.getDoctorApi().createDoctorSchedule(schedule).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<DoctorScheduleResponseDTO>> call, @NonNull Response<ResponseWrapper<DoctorScheduleResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<DoctorScheduleResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void getAllSchedules(int page, int limit, MutableLiveData<ResponseWrapper<List<DoctorScheduleResponseDTO>>> result) {
        ApiClient.getDoctorApi().getAllSchedules(page, limit).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<List<DoctorScheduleResponseDTO>>> call, @NonNull Response<ResponseWrapper<List<DoctorScheduleResponseDTO>>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<List<DoctorScheduleResponseDTO>>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void uploadCertification(MultipartBody.Part file, int doctorId, MutableLiveData<ResponseWrapper<Void>> result) {
        RequestBody doctorIdBody = RequestBody.create(String.valueOf(doctorId), MediaType.parse("text/plain"));
        ApiClient.getDoctorApi().uploadCertification(file, doctorIdBody).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Response<ResponseWrapper<Void>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void createAchievement(AchievementDTO achievement, MutableLiveData<ResponseWrapper<AchievementResponseDTO>> result) {
        ApiClient.getDoctorApi().createAchievement(achievement).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<AchievementResponseDTO>> call, @NonNull Response<ResponseWrapper<AchievementResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<AchievementResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void getAchievementById(int id, MutableLiveData<ResponseWrapper<AchievementResponseDTO>> result) {
        ApiClient.getDoctorApi().getAchievementById(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<AchievementResponseDTO>> call, @NonNull Response<ResponseWrapper<AchievementResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<AchievementResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void updateAchievement(int id, AchievementDTO achievement, MutableLiveData<ResponseWrapper<AchievementResponseDTO>> result) {
        ApiClient.getDoctorApi().updateAchievement(id, achievement).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<AchievementResponseDTO>> call, @NonNull Response<ResponseWrapper<AchievementResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<AchievementResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void deleteAchievement(int id, MutableLiveData<ResponseWrapper<Void>> result) {
        ApiClient.getDoctorApi().deleteAchievement(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Response<ResponseWrapper<Void>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void createSpecialty(SpecialtyDTO specialty, MutableLiveData<ResponseWrapper<SpecialtyResponseDTO>> result) {
        ApiClient.getDoctorApi().createSpecialty(specialty).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<SpecialtyResponseDTO>> call, @NonNull Response<ResponseWrapper<SpecialtyResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<SpecialtyResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void getSpecialtyById(int id, MutableLiveData<ResponseWrapper<SpecialtyResponseDTO>> result) {
        ApiClient.getDoctorApi().getSpecialtyById(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<SpecialtyResponseDTO>> call, @NonNull Response<ResponseWrapper<SpecialtyResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<SpecialtyResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void updateSpecialty(int id, SpecialtyDTO specialty, MutableLiveData<ResponseWrapper<SpecialtyResponseDTO>> result) {
        ApiClient.getDoctorApi().updateSpecialty(id, specialty).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<SpecialtyResponseDTO>> call, @NonNull Response<ResponseWrapper<SpecialtyResponseDTO>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<SpecialtyResponseDTO>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void deleteSpecialty(int id, MutableLiveData<ResponseWrapper<Void>> result) {
        ApiClient.getDoctorApi().deleteSpecialty(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Response<ResponseWrapper<Void>> response) {
                result.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<Void>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }

    public void getDoctorCertifications(int page, int limit, MutableLiveData<ResponseWrapper<List<CertificationResponseDTO>>> result) {
        ApiClient.getDoctorApi().getDoctorCertifications(page, limit).enqueue(new Callback<ResponseWrapper<List<CertificationResponseDTO>>>() {
            @Override
            public void onResponse(@NonNull Call<ResponseWrapper<List<CertificationResponseDTO>>> call, @NonNull Response<ResponseWrapper<List<CertificationResponseDTO>>> response) {
                result.setValue(response.body());
            }
            @Override
            public void onFailure(@NonNull Call<ResponseWrapper<List<CertificationResponseDTO>>> call, @NonNull Throwable t) {
                result.setValue(new ResponseWrapper<>("Network error", null));
            }
        });
    }
}

