package com.example.schedulemedical.model;

import com.google.gson.annotations.SerializedName;

public class Hospital {
    @SerializedName("hospitalId")
    private Integer hospitalId;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    // Getters and Setters
    public Integer getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
} 