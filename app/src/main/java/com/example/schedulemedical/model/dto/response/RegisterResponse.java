package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("message")
    private String message;
    
    @SerializedName("code")
    private Integer code;
    
    @SerializedName("success")
    private Boolean success;
    
    @SerializedName("data")
    private RegisterData data;
    
    public RegisterResponse() {}
    
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
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public RegisterData getData() {
        return data;
    }
    
    public void setData(RegisterData data) {
        this.data = data;
    }
    
    // Inner class for register data
    public static class RegisterData {
        @SerializedName("accountId")
        private String accountId;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName("fullName")
        private String fullName;
        
        @SerializedName("role")
        private String role;
        
        @SerializedName("status")
        private Boolean status;
        
        @SerializedName("createdAt")
        private String createdAt;
        
        public RegisterData() {}
        
        // Getters and Setters
        public String getAccountId() {
            return accountId;
        }
        
        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getFullName() {
            return fullName;
        }
        
        public void setFullName(String fullName) {
            this.fullName = fullName;
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
        
        public String getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }
} 