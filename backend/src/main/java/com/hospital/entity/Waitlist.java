package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Waitlist Entity for queueing patients when doctor slots are fully booked.
 * Supports Feature 3: Appointment Swap & Slot Marketplace.
 */
@Entity
@Table(name = "waitlist")
public class Waitlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Column(name = "preferred_time")
    private LocalTime preferredTime;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE"; // ACTIVE, WAITING, NOTIFIED, FULFILLED, CLAIMED, CANCELLED, EXPIRED

    @Column(length = 255)
    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Waitlist() {
    }

    public Waitlist(Patient patient, Doctor doctor, LocalDate preferredDate, LocalTime preferredTime) {
        this.patient = patient;
        this.doctor = doctor;
        this.preferredDate = preferredDate;
        this.preferredTime = preferredTime;
        this.status = "ACTIVE";
    }

    public Waitlist(Patient patient, Doctor doctor, LocalDate preferredDate, String notes) {
        this.patient = patient;
        this.doctor = doctor;
        this.preferredDate = preferredDate;
        this.notes = notes;
        this.status = "ACTIVE";
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
