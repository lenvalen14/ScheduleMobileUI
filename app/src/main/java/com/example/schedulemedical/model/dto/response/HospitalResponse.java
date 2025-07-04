package com.example.schedulemedical.model.dto.response;

import com.google.gson.annotations.SerializedName;

public class HospitalResponse {
    @SerializedName("hospitalId")
    private Integer hospitalId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("address")
    private String address;
    
    @SerializedName("phone")
    private String phone;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("establishYear")
    private Integer establishYear;
    
    @SerializedName("logo")
    private String logo;
    
    @SerializedName("workScheduling")
    private String workScheduling;
    
    @SerializedName("type")
    private String type;
    
    @SerializedName("website")
    private String website;
    
    @SerializedName("certificates")
    private String certificates;
    
    @SerializedName("gallery")
    private String gallery;
    
    @SerializedName("latitude")
    private Float latitude;
    
    @SerializedName("longitude")
    private Float longitude;
    
    @SerializedName("rating")
    private Float rating;
    
    @SerializedName("reviews")
    private Integer reviews;
    
    @SerializedName("totalBeds")
    private Integer totalBeds;
    
    @SerializedName("totalNurses")
    private Integer totalNurses;
    
    @SerializedName("verified")
    private Boolean verified;
    
    // Additional calculated fields that might come from API
    @SerializedName("distance")
    private Float distance;
    
    @SerializedName("doctorCount")
    private Integer doctorCount;
    
    public HospitalResponse() {}
    
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
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getEstablishYear() {
        return establishYear;
    }
    
    public void setEstablishYear(Integer establishYear) {
        this.establishYear = establishYear;
    }
    
    public String getLogo() {
        return logo;
    }
    
    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    public String getWorkScheduling() {
        return workScheduling;
    }
    
    public void setWorkScheduling(String workScheduling) {
        this.workScheduling = workScheduling;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public String getCertificates() {
        return certificates;
    }
    
    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }
    
    public String getGallery() {
        return gallery;
    }
    
    public void setGallery(String gallery) {
        this.gallery = gallery;
    }
    
    public Float getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }
    
    public Float getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }
    
    public Float getRating() {
        return rating;
    }
    
    public void setRating(Float rating) {
        this.rating = rating;
    }
    
    public Integer getReviews() {
        return reviews;
    }
    
    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }
    
    public Integer getTotalBeds() {
        return totalBeds;
    }
    
    public void setTotalBeds(Integer totalBeds) {
        this.totalBeds = totalBeds;
    }
    
    public Integer getTotalNurses() {
        return totalNurses;
    }
    
    public void setTotalNurses(Integer totalNurses) {
        this.totalNurses = totalNurses;
    }
    
    public Boolean getVerified() {
        return verified;
    }
    
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
    
    public Float getDistance() {
        return distance;
    }
    
    public void setDistance(Float distance) {
        this.distance = distance;
    }
    
    public Integer getDoctorCount() {
        return doctorCount;
    }
    
    public void setDoctorCount(Integer doctorCount) {
        this.doctorCount = doctorCount;
    }
    
    // Helper methods
    public String getFormattedRating() {
        if (rating != null) {
            return String.format("%.1f", rating);
        }
        return "0.0";
    }
    
    public String getFormattedDistance() {
        if (distance != null) {
            if (distance < 1) {
                return String.format("%.0f m", distance * 1000);
            } else {
                return String.format("%.1f km", distance);
            }
        }
        return "";
    }
    
    public String getFormattedReviews() {
        if (reviews != null && reviews > 0) {
            return "(" + reviews + " đánh giá)";
        }
        return "(0 đánh giá)";
    }
} 