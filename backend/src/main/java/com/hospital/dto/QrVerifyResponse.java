package com.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class QrVerifyResponse {
    private boolean valid;
    private String message;
    private Long appointmentId;
    private String patientName;
    private String doctorName;
    private String specialization;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;

    public QrVerifyResponse() {
    }

    public static QrVerifyResponse invalid(String message) {
        QrVerifyResponse resp = new QrVerifyResponse();
        resp.setValid(false);
        resp.setMessage(message);
        return resp;
    }

    public static QrVerifyResponse valid(Long appointmentId, String patientName, String doctorName,
                                         String specialization, LocalDate appointmentDate, LocalTime appointmentTime,
                                         String status, String message) {
        QrVerifyResponse resp = new QrVerifyResponse();
        resp.setValid(true);
        resp.setAppointmentId(appointmentId);
        resp.setPatientName(patientName);
        resp.setDoctorName(doctorName);
        resp.setSpecialization(specialization);
        resp.setAppointmentDate(appointmentDate);
        resp.setAppointmentTime(appointmentTime);
        resp.setStatus(status);
        resp.setMessage(message);
        return resp;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
