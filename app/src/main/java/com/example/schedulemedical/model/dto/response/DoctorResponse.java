package com.example.schedulemedical.model.dto.response;

import java.util.List;

public class DoctorResponse {
    private Integer doctorId;
    private Integer userId;
    private Integer specialtyId;
    private Integer hospitalId;
    private Float rating;
    private String bio;
    private String yearsOfExperience;
    private String education;
    private String clinic;
    private Integer slotCapacity;

    private UserResponse user;
    private SpecialtyResponse specialty;
    private HospitalResponse hospital;
    private List<ScheduleResponse> schedules;
    private List<AppointmentResponse> appointments;
    private List<String> achievements;

    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(Integer specialtyId) { this.specialtyId = specialtyId; }

    public Integer getHospitalId() { return hospitalId; }
    public void setHospitalId(Integer hospitalId) { this.hospitalId = hospitalId; }

    public Float getRating() { return rating; }
    public void setRating(Float rating) { this.rating = rating; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(String yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getClinic() { return clinic; }
    public void setClinic(String clinic) { this.clinic = clinic; }

    public Integer getSlotCapacity() { return slotCapacity; }
    public void setSlotCapacity(Integer slotCapacity) { this.slotCapacity = slotCapacity; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public SpecialtyResponse getSpecialty() { return specialty; }
    public void setSpecialty(SpecialtyResponse specialty) { this.specialty = specialty; }

    public HospitalResponse getHospital() { return hospital; }
    public void setHospital(HospitalResponse hospital) { this.hospital = hospital; }

    public List<ScheduleResponse> getSchedules() { return schedules; }
    public void setSchedules(List<ScheduleResponse> schedules) { this.schedules = schedules; }

    public List<AppointmentResponse> getAppointments() { return appointments; }
    public void setAppointments(List<AppointmentResponse> appointments) { this.appointments = appointments; }

    public List<String> getAchievements() { return achievements; }
    public void setAchievements(List<String> achievements) { this.achievements = achievements; }
}