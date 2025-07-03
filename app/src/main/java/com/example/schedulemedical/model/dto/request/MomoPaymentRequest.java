package com.example.schedulemedical.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class MomoPaymentRequest {
    @SerializedName("amount")
    private Double amount;
    
    @SerializedName("orderId")
    private String orderId;
    
    @SerializedName("orderInfo")
    private String orderInfo;
    
    @SerializedName("appointmentId")
    private Integer appointmentId;
    
    @SerializedName("paymentMethod")
    private String paymentMethod; // "MOMO"
    
    @SerializedName("paymentStatus")
    private String paymentStatus; // "PENDING"
    
    public MomoPaymentRequest() {}
    
    public MomoPaymentRequest(Double amount, String orderId, String orderInfo, 
                             Integer appointmentId, String paymentMethod, String paymentStatus) {
        this.amount = amount;
        this.orderId = orderId;
        this.orderInfo = orderInfo;
        this.appointmentId = appointmentId;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }
    
    // Getters and Setters
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderInfo() {
        return orderInfo;
    }
    
    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
    
    public Integer getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
} 