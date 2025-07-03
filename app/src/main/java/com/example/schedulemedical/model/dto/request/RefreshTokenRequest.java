package com.example.schedulemedical.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequest {
    @SerializedName("refreshToken")
    private String refreshToken;
    
    @SerializedName("userId")
    private Integer userId;
    
    public RefreshTokenRequest() {}
    
    public RefreshTokenRequest(String refreshToken, Integer userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
} 