package com.example.schedulemedical.model.dto.response.doctor;

import com.example.schedulemedical.model.dto.request.doctor.UserDTO;

public class DoctorResponseDTO {
    private int doctorId;
    private String fullName;
    private String specialty;
    private String hospital;
    private float rating;
    private String bio;
    private String yearsOfExperience;
    private String education;
    private String clinic;
    private UserDTO user;
}