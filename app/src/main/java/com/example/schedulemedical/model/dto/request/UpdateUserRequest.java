package com.example.schedulemedical.model.dto.request;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("dateOfBirth")
    private String dateOfBirth;
    @SerializedName("address")
    private String address;
    @SerializedName("nationalId")
    private String nationalId;
    @SerializedName("ethnicity")
    private String ethnicity;
    @SerializedName("gender")
    private String gender;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
} 