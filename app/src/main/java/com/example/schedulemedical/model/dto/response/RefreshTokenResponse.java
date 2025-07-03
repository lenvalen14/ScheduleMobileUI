package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenResponse {
    @SerializedName("accessToken")
    private String accessToken;
    
    @SerializedName("refreshToken")
    private String refreshToken;
    
    @SerializedName("user")
    private UserData user;
    
    public RefreshTokenResponse() {}
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public UserData getUser() {
        return user;
    }
    
    public void setUser(UserData user) {
        this.user = user;
    }
    
    // Inner class for user data
    public static class UserData {
        @SerializedName("userId")
        private Integer userId;
        
        @SerializedName("fullName")
        private String fullName;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("role")
        private String role;
        
        public UserData() {}
        
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
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
} 