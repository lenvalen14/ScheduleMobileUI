package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("userId")
    private int userId;
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("phone")
    private String phone;
    @SerializedName("email")
    private String email;
    @SerializedName("gender")
    private String gender;
    @SerializedName("address")
    private String address;
    @SerializedName("dateOfBirth")
    private String dateOfBirth;
    @SerializedName("ethnicity")
    private String ethnicity;
    @SerializedName("nationalId")
    private String nationalId;

    @SerializedName("avatar")
    private String avatar;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getEthnicity() { return ethnicity; }
    public void setEthnicity(String ethnicity) { this.ethnicity = ethnicity; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public String getAvatar() { return avatar; }
}
