package com.example.schedulemedical.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class CreateAppointmentRequest {
    @SerializedName("doctorId")
    private Integer doctorId;
    
    @SerializedName("userId")
    private Integer userId;
    
    @SerializedName("serviceId")
    private Integer serviceId;
    
    @SerializedName("scheduledTime")
    private String scheduledTime; // ISO 8601 format
    
    @SerializedName("note")
    private String note;
    
    @SerializedName("status")
    private String status; // "SCHEDULED", "PENDING", etc.
    
    public CreateAppointmentRequest() {}
    
    public CreateAppointmentRequest(Integer doctorId, Integer userId, Integer serviceId, 
                                  String scheduledTime, String note, String status) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.serviceId = serviceId;
        this.scheduledTime = scheduledTime;
        this.note = note;
        this.status = status;
    }
    
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
    
    public Integer getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
} 