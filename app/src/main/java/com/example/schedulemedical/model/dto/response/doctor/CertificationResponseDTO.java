package com.example.schedulemedical.model.dto.response.doctor;

public class CertificationResponseDTO {
    private int certificationId;
    private String fileUrl;
    private int doctorId;

    public int getCertificationId() { return certificationId; }
    public String getFileUrl() { return fileUrl; }
    public int getDoctorId() { return doctorId; }
}
