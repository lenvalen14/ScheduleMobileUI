package com.example.schedulemedical.ui.doctorprofile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.schedulemedical.data.repository.DoctorRepository;
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

import java.util.List;

import okhttp3.MultipartBody;

//UI → ViewModel → Repository → Api → Server → Response → UI
public class DoctorViewModel extends ViewModel {

    private final DoctorRepository repository = new DoctorRepository();

    // Doctor
    public MutableLiveData<List<DoctorResponseDTO>> topRatedDoctors = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<List<DoctorResponseDTO>>> doctorsBySpecialty = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<DoctorResponseDTO>> doctorByUserId = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<DoctorResponseDTO>> createdDoctor = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<DoctorResponseDTO>> updatedDoctor = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<Void>> updatedRating = new MutableLiveData<>();

    // Schedule
    public MutableLiveData<ResponseWrapper<DoctorScheduleResponseDTO>> createdSchedule = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<List<DoctorScheduleResponseDTO>>> schedules = new MutableLiveData<>();

    // Certification
    public MutableLiveData<ResponseWrapper<Void>> uploadCertResult = new MutableLiveData<>();

    // Achievement
    public MutableLiveData<ResponseWrapper<AchievementResponseDTO>> createdAchievement = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<AchievementResponseDTO>> achievementById = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<AchievementResponseDTO>> updatedAchievement = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<Void>> deletedAchievement = new MutableLiveData<>();

    // Specialty
    public MutableLiveData<ResponseWrapper<SpecialtyResponseDTO>> createdSpecialty = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<SpecialtyResponseDTO>> specialtyById = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<SpecialtyResponseDTO>> updatedSpecialty = new MutableLiveData<>();
    public MutableLiveData<ResponseWrapper<Void>> deletedSpecialty = new MutableLiveData<>();

    // ========================
    //        CALLS
    // ========================

    public void loadTopRatedDoctors() {
        repository.getTopRatedDoctors(topRatedDoctors);
    }

    public void loadDoctorsBySpecialty(int specialtyId, int page, int limit) {
        repository.getDoctorsBySpecialty(specialtyId, page, limit, doctorsBySpecialty);
    }

    public void loadDoctorByUserId(int userId) {
        repository.getDoctorByUserId(userId, doctorByUserId);
    }

    public void createDoctor(CreateDoctorDTO doctor) {
        repository.createDoctor(doctor, createdDoctor);
    }

    public void updateDoctor(int id, UpdateDoctorDTO doctor) {
        repository.updateDoctor(id, doctor, updatedDoctor);
    }

    public void updateDoctorRating(int id, float rating) {
        repository.updateDoctorRating(id, rating, updatedRating);
    }

    public void createDoctorSchedule(DoctorScheduleDTO schedule) {
        repository.createDoctorSchedule(schedule, createdSchedule);
    }

    public void loadAllSchedules(int page, int limit) {
        repository.getAllSchedules(page, limit, schedules);
    }

    public void uploadCertification(MultipartBody.Part file, int doctorId) {
        repository.uploadCertification(file, doctorId, uploadCertResult);
    }

    public void createAchievement(AchievementDTO dto) {
        repository.createAchievement(dto, createdAchievement);
    }

    public void loadAchievementById(int id) {
        repository.getAchievementById(id, achievementById);
    }

    public void updateAchievement(int id, AchievementDTO dto) {
        repository.updateAchievement(id, dto, updatedAchievement);
    }

    public void deleteAchievement(int id) {
        repository.deleteAchievement(id, deletedAchievement);
    }

    public void createSpecialty(SpecialtyDTO dto) {
        repository.createSpecialty(dto, createdSpecialty);
    }

    public void loadSpecialtyById(int id) {
        repository.getSpecialtyById(id, specialtyById);
    }

    public void updateSpecialty(int id, SpecialtyDTO dto) {
        repository.updateSpecialty(id, dto, updatedSpecialty);
    }

    public void deleteSpecialty(int id) {
        repository.deleteSpecialty(id, deletedSpecialty);
    }
}
