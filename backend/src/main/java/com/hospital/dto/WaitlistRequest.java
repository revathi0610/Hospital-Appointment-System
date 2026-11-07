package com.hospital.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class WaitlistRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotNull(message = "Preferred date is required")
    private LocalDate preferredDate;

    private LocalTime preferredTime;

    private String notes;

    public WaitlistRequest() {
    }

    public WaitlistRequest(Long patientId, Long doctorId, LocalDate preferredDate, LocalTime preferredTime) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
    }

    public WaitlistRequest(Long patientId, Long doctorId, LocalDate preferredDate, String notes) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.preferredDate = preferredDate;
        this.notes = notes;
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

    public LocalDate getPreferredDate() {
        return preferredDate;
    }

    public void setPreferredDate(LocalDate preferredDate) {
        this.preferredDate = preferredDate;
    }

    public LocalTime getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(LocalTime preferredTime) {
        this.preferredTime = preferredTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
