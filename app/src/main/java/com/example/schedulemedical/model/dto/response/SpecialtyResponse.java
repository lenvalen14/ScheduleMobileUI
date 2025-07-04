package com.example.schedulemedical.model.dto.response;

public class SpecialtyResponse {
    private Integer specialtyId;
    private String name;
    private String description;

    public Integer getSpecialtyId() { return specialtyId; }
    public void setSpecialtyId(Integer specialtyId) { this.specialtyId = specialtyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
