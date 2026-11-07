package com.hospital.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReleasedSlotDto {
    private Long appointmentId;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String consultationRoom;
    private BigDecimal consultationFee;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String originalReason;
    private String originalPatientName;
    private String status;

    public ReleasedSlotDto() {
    }

    public ReleasedSlotDto(Long appointmentId, Long doctorId, String doctorName,
                           String specialization, String consultationRoom, BigDecimal consultationFee,
                           LocalDate appointmentDate, LocalTime appointmentTime,
                           String originalReason, String originalPatientName, String status) {
        this.appointmentId = appointmentId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.consultationRoom = consultationRoom;
        this.consultationFee = consultationFee;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.originalReason = originalReason;
        this.originalPatientName = originalPatientName;
        this.status = status;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getConsultationRoom() {
        return consultationRoom;
    }

    public void setConsultationRoom(String consultationRoom) {
        this.consultationRoom = consultationRoom;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getOriginalReason() {
        return originalReason;
    }

    public void setOriginalReason(String originalReason) {
        this.originalReason = originalReason;
    }

    public String getOriginalPatientName() {
        return originalPatientName;
    }

    public void setOriginalPatientName(String originalPatientName) {
        this.originalPatientName = originalPatientName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
