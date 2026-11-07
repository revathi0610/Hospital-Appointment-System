package com.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class DigitalCardDto {
    private Long appointmentId;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private String doctorName;
    private String doctorSpecialization;
    private String doctorDepartment;
    private String consultationRoom;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String qrToken;
    private String qrDataString; // JSON or verification text encoded in QR
    private int estimatedWaitMinutes;

    public DigitalCardDto() {
    }

    public DigitalCardDto(Long appointmentId, String patientName, String patientEmail, String patientPhone,
                          String doctorName, String doctorSpecialization, String doctorDepartment,
                          String consultationRoom, LocalDate appointmentDate, LocalTime appointmentTime,
                          String status, String qrToken, String qrDataString, int estimatedWaitMinutes) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.doctorName = doctorName;
        this.doctorSpecialization = doctorSpecialization;
        this.doctorDepartment = doctorDepartment;
        this.consultationRoom = consultationRoom;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.qrToken = qrToken;
        this.qrDataString = qrDataString;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getDoctorDepartment() {
        return doctorDepartment;
    }

    public void setDoctorDepartment(String doctorDepartment) {
        this.doctorDepartment = doctorDepartment;
    }

    public String getConsultationRoom() {
        return consultationRoom;
    }

    public void setConsultationRoom(String consultationRoom) {
        this.consultationRoom = consultationRoom;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public String getQrDataString() {
        return qrDataString;
    }

    public void setQrDataString(String qrDataString) {
        this.qrDataString = qrDataString;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public void setEstimatedWaitMinutes(int estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }
}
