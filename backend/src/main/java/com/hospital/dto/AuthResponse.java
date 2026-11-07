package com.hospital.dto;

public class AuthResponse {
    private Long userId;
    private String name;
    private String email;
    private String role;
    private Long patientId;
    private Long doctorId;
    private String phone;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(Long userId, String name, String email, String role, Long patientId, Long doctorId, String phone, String token) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.phone = phone;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
