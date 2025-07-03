package com.example.schedulemedical.model.dto.response;

import java.util.List;

public class DoctorResponse {
    private Integer id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
    private String bio;
    private String experience;
    private String rating;
    private Integer totalReviews;
    private String hospitalName;
    private String departmentName;
    private String specialtyName;
    private Boolean isActive;
    private String address;
    private List<String> languages;
    private List<String> certifications;
    private List<String> achievements;
    private String consultationFee;
    private String workingHours;
    
    public DoctorResponse() {}
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public String getRating() {
        return rating;
    }
    
    public void setRating(String rating) {
        this.rating = rating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public String getHospitalName() {
        return hospitalName;
    }
    
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    
    public String getDepartmentName() {
        return departmentName;
    }
    
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    
    public String getSpecialtyName() {
        return specialtyName;
    }
    
    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public List<String> getLanguages() {
        return languages;
    }
    
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    
    public List<String> getCertifications() {
        return certifications;
    }
    
    public void setCertifications(List<String> certifications) {
        this.certifications = certifications;
    }
    
    public List<String> getAchievements() {
        return achievements;
    }
    
    public void setAchievements(List<String> achievements) {
        this.achievements = achievements;
    }
    
    public String getConsultationFee() {
        return consultationFee;
    }
    
    public void setConsultationFee(String consultationFee) {
        this.consultationFee = consultationFee;
    }
    
    public String getWorkingHours() {
        return workingHours;
    }
    
    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }
} 