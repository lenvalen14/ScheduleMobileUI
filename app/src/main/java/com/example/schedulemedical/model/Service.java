package com.example.schedulemedical.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Service {
    @SerializedName("serviceId")
    private Integer serviceId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("price")
    private Double price;
    
    @SerializedName("duration")
    private String duration;
    
    @SerializedName("highlighted")
    private Boolean highlighted;
    
    @SerializedName("description")
    private List<String> description;
    
    @SerializedName("calender")
    private String calendar;
    
    public Service() {}
    
    public Service(Integer serviceId, String name, Double price, String duration, 
                  Boolean highlighted, List<String> description, String calendar) {
        this.serviceId = serviceId;
        this.name = name;
        this.price = price;
        this.duration = duration;
        this.highlighted = highlighted;
        this.description = description;
        this.calendar = calendar;
    }
    
    // Getters and Setters
    public Integer getServiceId() {
        return serviceId;
    }
    
    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public Boolean getHighlighted() {
        return highlighted;
    }
    
    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }
    
    public List<String> getDescription() {
        return description;
    }
    
    public void setDescription(List<String> description) {
        this.description = description;
    }
    
    public String getCalendar() {
        return calendar;
    }
    
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }
    
    public String getFormattedPrice() {
        if (price == null) return "0 VND";
        return String.format("%.0f VND", price);
    }
    
    public String getServiceName() {
        return name;
    }
} 