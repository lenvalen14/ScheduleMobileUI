package com.example.schedulemedical.model.dto.request.appoiment;

import java.time.LocalDateTime;

public class AppointmentDTO {
    private Integer appointmentId;
    private Integer doctorId;
    private Integer userId;
    private Integer serviceId;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private String note;
    private AppointmentStatus status;

    // Constructors
    public AppointmentDTO() {}

    public AppointmentDTO(Integer doctorId, Integer userId, Integer serviceId,
                          LocalDateTime scheduledTime, String note) {
        this.doctorId = doctorId;
        this.userId = userId;
        this.serviceId = serviceId;
        this.scheduledTime = scheduledTime;
        this.note = note;
        this.status = AppointmentStatus.PENDING;
    }

    // Getters and Setters
    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    // Appointment Status Enum
    public enum AppointmentStatus {
        PENDING("PENDING"),
        CONFIRMED("CONFIRMED"),
        COMPLETED("COMPLETED"),
        CANCELLED("CANCELLED"),
        NO_SHOW("NO_SHOW");

        private final String value;

        AppointmentStatus(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static AppointmentStatus fromString(String text) {
            for (AppointmentStatus status : AppointmentStatus.values()) {
                if (status.value.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }
}
