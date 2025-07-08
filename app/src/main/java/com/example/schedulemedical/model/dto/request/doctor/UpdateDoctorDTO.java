package com.example.schedulemedical.model.dto.request.doctor;

public class UpdateDoctorDTO {private String bio;
    private String yearsOfExperience;
    private String education;
    private String clinic;
    private Integer slotCapacity;
    private Integer specialtyId;
    private Integer hospitalId;

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(String yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getClinic() { return clinic; }
    public void setClinic(String clinic) { this.clinic = clinic; }

    public Integer getSlotCapacity() { return slotCapacity; }
    public void setSlotCapacity(Integer slotCapacity) { this.slotCapacity = slotCapacity; }

    public Integer getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(Integer specialtyId) { this.specialtyId = specialtyId; }

    public Integer getHospitalId() { return hospitalId; }
    public void setHospitalId(Integer hospitalId) { this.hospitalId = hospitalId; }
}
