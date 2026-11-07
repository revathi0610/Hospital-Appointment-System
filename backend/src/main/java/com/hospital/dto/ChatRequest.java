package com.hospital.dto;

import java.util.List;

public class ChatRequest {
    private String message;
    private Long patientId;
    private Long doctorId;

    public ChatRequest() {
    }

    public ChatRequest(String message, Long patientId, Long doctorId) {
        this.message = message;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
