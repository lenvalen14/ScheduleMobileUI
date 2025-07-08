package com.example.schedulemedical.model.dto.response;

import java.util.List;

public class AppointmentResponse {
    private Integer appointmentId;
    private Integer doctorId;
    private Integer userId;
    private Integer serviceId;
    private String scheduledTime;
    private String createdAt;
    private String note;
    private String status;

    private Doctor doctor;
    private User user;
    private Service service;
    private Object feedback;
    private List<Object> followUps;
    private List<Object> payments;

    public Integer getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Integer appointmentId) { this.appointmentId = appointmentId; }

    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Service getService() { return service; }
    public void setService(Service service) { this.service = service; }

    public Object getFeedback() { return feedback; }
    public void setFeedback(Object feedback) { this.feedback = feedback; }

    public List<Object> getFollowUps() { return followUps; }
    public void setFollowUps(List<Object> followUps) { this.followUps = followUps; }

    public List<Object> getPayments() { return payments; }
    public void setPayments(List<Object> payments) { this.payments = payments; }

    // Inner classes for Doctor, User, Service (simplified, add more fields as needed)
    public static class Doctor {
        private Integer doctorId;
        private Integer userId;
        private Integer specialtyId;
        private Integer hospitalId;
        private Integer rating;
        private String bio;
        private String yearsOfExperience;
        private String education;
        private String clinic;
        private Integer slotCapacity;
        private User user;
        private Specialty specialty;
        // ... schedules, etc.
        public Integer getDoctorId() { return doctorId; }
        public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public Specialty getSpecialty() { return specialty; }
        public void setSpecialty(Specialty specialty) { this.specialty = specialty; }
        // ... other getters/setters
    }
    public static class User {
        private Integer userId;
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private String dateOfBirth;
        private String nationalId;
        private String ethnicity;
        private String password;
        private String role;
        private String gender;
        private String createdAt;
        private String avatar;
        private Boolean isActive;
        // ... other fields
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        // ... other getters/setters
    }
    public static class Service {
        private Integer serviceId;
        private String name;
        private Integer price;
        private String duration;
        private Boolean highlighted;
        private List<String> description;
        private String calender;
        // ... other fields
        public Integer getServiceId() { return serviceId; }
        public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public List<String> getDescription() { return description; }
        public void setDescription(List<String> description) { this.description = description; }
        // ... other getters/setters
    }
    public static class Specialty {
        private Integer specialtyId;
        private String name;
        private String description;
        public Integer getSpecialtyId() { return specialtyId; }
        public void setSpecialtyId(Integer specialtyId) { this.specialtyId = specialtyId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        // ...
    }
}
