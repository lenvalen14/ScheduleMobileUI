package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("message")
    private String message;
    
    @SerializedName("code")
    private Integer code;
    
    @SerializedName("data")
    private LoginData data;
    
    public LoginResponse() {}
    
    // Getters and Setters
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public LoginData getData() {
        return data;
    }
    
    public void setData(LoginData data) {
        this.data = data;
    }
    
    // Inner class for login data
    public static class LoginData {
        @SerializedName("sub")
        private Integer sub; // user ID
        
        @SerializedName("accessToken")
        private String accessToken;
        
        @SerializedName("refreshToken")
        private String refreshToken;
        
        public LoginData() {}
        
        // Getters and Setters
        public Integer getSub() {
            return sub;
        }
        
        public void setSub(Integer sub) {
            this.sub = sub;
        }
        
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
    }
} 