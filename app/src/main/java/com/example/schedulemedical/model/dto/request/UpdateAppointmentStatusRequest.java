package com.example.schedulemedical.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class UpdateAppointmentStatusRequest {
    @SerializedName("status")
    private String status; // "SCHEDULED", "COMPLETED", "CANCELLED", "RESCHEDULED", "PENDING"
    
    public UpdateAppointmentStatusRequest() {}
    
    public UpdateAppointmentStatusRequest(String status) {
        this.status = status;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
} 