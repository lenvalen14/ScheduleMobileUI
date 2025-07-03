package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {
    @SerializedName("gender")
    private String gender;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("userId")
    private String userId;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("role")
    private String role;
    
    @SerializedName("status")
    private Boolean status;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("dateOfBirth")
    private String dateOfBirth;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("nationalId")
    private String nationalId;
    
    @SerializedName("ethnicity")
    private String ethnicity;
    
    @SerializedName("avatar")
    private String avatar;
    
    public ProfileResponse() {}
    
    // Getters and Setters
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Boolean getStatus() {
        return status;
    }
    
    public void setStatus(Boolean status) {
        this.status = status;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getNationalId() {
        return nationalId;
    }
    
    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }
    
    public String getEthnicity() {
        return ethnicity;
    }
    
    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
} 