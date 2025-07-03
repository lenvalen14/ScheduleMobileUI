package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("message")
    private String message;
    
    @SerializedName("code")
    private Integer code;
    
    @SerializedName("data")
    private T data;
    
    @SerializedName("success")
    private Boolean success;
    
    public ApiResponse() {}
    
    public ApiResponse(String message, Integer code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }
    
    public ApiResponse(String message, Integer code, T data, Boolean success) {
        this.message = message;
        this.code = code;
        this.data = data;
        this.success = success;
    }
    
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
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    // Helper methods
    public boolean isSuccessful() {
        return code != null && code >= 200 && code < 300;
    }
    
    public boolean hasData() {
        return data != null;
    }
} 