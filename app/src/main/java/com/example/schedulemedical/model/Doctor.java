package com.example.schedulemedical.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Doctor {
    @SerializedName("doctorId")
    private Integer doctorId;
    
    @SerializedName("userId")
    private Integer userId;
    
    @SerializedName("specialtyId")
    private Integer specialtyId;
    
    @SerializedName("hospitalId")
    private Integer hospitalId;
    
    @SerializedName("rating")
    private Double rating;
    
    @SerializedName("bio")
    private String bio;
    
    @SerializedName("yearsOfExperience")
    private Integer yearsOfExperience;
    
    @SerializedName("education")
    private String education;
    
    @SerializedName("clinic")
    private String clinic;
    
    @SerializedName("slotCapacity")
    private Integer slotCapacity;
    
    @SerializedName("user")
    private User user;
    
    @SerializedName("specialty")
    private Specialty specialty;
    
    @SerializedName("hospital")
    private Hospital hospital;
    
    @SerializedName("schedules")
    private List<Schedule> schedules;
    
    public Doctor() {}
    
    // Getters and Setters
    public Integer getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getSpecialtyId() {
        return specialtyId;
    }
    
    public void setSpecialtyId(Integer specialtyId) {
        this.specialtyId = specialtyId;
    }
    
    public Integer getHospitalId() {
        return hospitalId;
    }
    
    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public String getEducation() {
        return education;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public String getClinic() {
        return clinic;
    }
    
    public void setClinic(String clinic) {
        this.clinic = clinic;
    }
    
    public Integer getSlotCapacity() {
        return slotCapacity;
    }
    
    public void setSlotCapacity(Integer slotCapacity) {
        this.slotCapacity = slotCapacity;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Specialty getSpecialty() {
        return specialty;
    }
    
    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }
    
    public Hospital getHospital() {
        return hospital;
    }
    
    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }
    
    public List<Schedule> getSchedules() {
        return schedules;
    }
    
    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }
    
    // Helper methods
    public String getFullName() {
        if (user != null && user.getFullName() != null) {
            return "BS. " + user.getFullName();
        }
        return "Bác sĩ";
    }
    
    public String getSpecialtyName() {
        if (specialty != null && specialty.getName() != null) {
            return specialty.getName();
        }
        return "Chuyên khoa";
    }
    
    public String getHospitalName() {
        if (hospital != null && hospital.getName() != null) {
            return hospital.getName();
        }
        return "Bệnh viện";
    }
    
    public String getRatingText() {
        if (rating != null && rating > 0) {
            return String.format("%.1f ⭐", rating);
        }
        return "Chưa có đánh giá";
    }
    
    public String getExperienceText() {
        if (yearsOfExperience != null && yearsOfExperience > 0) {
            return yearsOfExperience + " năm kinh nghiệm";
        }
        return "Kinh nghiệm chưa cập nhật";
    }
}

// Supporting models
class User {
    @SerializedName("userId")
    private Integer userId;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

class Hospital {
    @SerializedName("hospitalId")
    private Integer hospitalId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("address")
    private String address;
    
    // Getters and Setters
    public Integer getHospitalId() {
        return hospitalId;
    }
    
    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
}

class Schedule {
    @SerializedName("scheduleId")
    private Integer scheduleId;
    
    @SerializedName("doctorId")
    private Integer doctorId;
    
    @SerializedName("dayOfWeek")
    private Integer dayOfWeek;
    
    @SerializedName("startTime")
    private String startTime;
    
    @SerializedName("endTime")
    private String endTime;
    
    // Getters and Setters
    public Integer getScheduleId() {
        return scheduleId;
    }
    
    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public Integer getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }
    
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
} 