package com.example.schedulemedical.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class LogoutRequest {
    @SerializedName("refreshToken")
    private String refreshToken;
    
    public LogoutRequest() {}
    
    public LogoutRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
} 