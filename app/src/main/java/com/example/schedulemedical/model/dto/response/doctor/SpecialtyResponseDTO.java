package com.example.schedulemedical.model.dto.response.doctor;

public class SpecialtyResponseDTO {
    private int specialtyId;
    private String name;
    private String description;

    public SpecialtyResponseDTO() {
    }

    public int getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(int specialtyId) {
        this.specialtyId = specialtyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
