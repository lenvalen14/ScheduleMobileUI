package com.example.schedulemedical.model;

import com.google.gson.annotations.SerializedName;

public class Specialty {
    @SerializedName("id")
    private Integer specialtyId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("doctorCount")
    private Integer doctorCount;
    
    public Specialty() {}
    
    public Specialty(Integer specialtyId, String name, String description, Integer doctorCount) {
        this.specialtyId = specialtyId;
        this.name = name;
        this.description = description;
        this.doctorCount = doctorCount;
    }
    
    // Getters and Setters
    public Integer getSpecialtyId() {
        return specialtyId;
    }
    
    public void setSpecialtyId(Integer specialtyId) {
        this.specialtyId = specialtyId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getDoctorCount() {
        return doctorCount;
    }
    
    public void setDoctorCount(Integer doctorCount) {
        this.doctorCount = doctorCount;
    }
    
    public String getDoctorCountText() {
        if (doctorCount == null || doctorCount == 0) {
            return "Chưa có bác sĩ";
        }
        return doctorCount + " bác sĩ";
    }
} 