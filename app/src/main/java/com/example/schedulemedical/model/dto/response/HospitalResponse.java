package com.example.schedulemedical.model.dto.response;

public class HospitalResponse {
    private Integer hospitalId;
    private String name;
    private String address;
    private String phone;
    private String description;
    private String email;
    private Integer establishYear;
    private String logo;
    private String workScheduling;
    private String type;
    private String website;
    private Double rating;
    private Integer reviews;
    private Integer totalBeds;
    private Integer totalNurses;
    private Boolean verified;

    public Integer getHospitalId() { return hospitalId; }
    public void setHospitalId(Integer hospitalId) { this.hospitalId = hospitalId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getEstablishYear() { return establishYear; }
    public void setEstablishYear(Integer establishYear) { this.establishYear = establishYear; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public String getWorkScheduling() { return workScheduling; }
    public void setWorkScheduling(String workScheduling) { this.workScheduling = workScheduling; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviews() { return reviews; }
    public void setReviews(Integer reviews) { this.reviews = reviews; }

    public Integer getTotalBeds() { return totalBeds; }
    public void setTotalBeds(Integer totalBeds) { this.totalBeds = totalBeds; }

    public Integer getTotalNurses() { return totalNurses; }
    public void setTotalNurses(Integer totalNurses) { this.totalNurses = totalNurses; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
}
