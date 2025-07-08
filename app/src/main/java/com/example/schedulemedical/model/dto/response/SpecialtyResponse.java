package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class SpecialtyResponse {
    @SerializedName("id")
    private Integer specialtyId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("doctorCount")
    private Integer doctorCount;

    public Integer getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(Integer specialtyId) { this.specialtyId = specialtyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDoctorCount() { return doctorCount; }
    public void setDoctorCount(Integer doctorCount) { this.doctorCount = doctorCount; }
}
