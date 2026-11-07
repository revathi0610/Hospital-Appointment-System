package com.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class WaitingTimeDto {
    private Long appointmentId;
    private LocalDate appointmentDate;
    private LocalTime scheduledTime;
    private LocalTime expectedConsultationTime;
    private String doctorName;
    private String doctorSpecialization;
    private String consultationRoom;
    private int patientsAhead;
    private int avgConsultationDuration = 15; // in minutes
    private int estimatedWaitMinutes;
    private String estimatedWaitFormatted;
    private String statusMessage;
    private String notice = "Estimated waiting time. Actual waiting time may vary based on clinical complexity.";

    public WaitingTimeDto() {
    }

    public WaitingTimeDto(Long appointmentId, int patientsAhead, int avgConsultationDuration, int estimatedWaitMinutes) {
        this.appointmentId = appointmentId;
        this.patientsAhead = patientsAhead;
        this.avgConsultationDuration = avgConsultationDuration;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.estimatedWaitFormatted = formatDuration(estimatedWaitMinutes);
    }

    private String formatDuration(int minutes) {
        if (minutes <= 0) return "0 mins (Next in line)";
        if (minutes < 60) return minutes + " mins";
        int hours = minutes / 60;
        int rem = minutes % 60;
        return hours + "h " + (rem > 0 ? rem + "m" : "");
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalTime getExpectedConsultationTime() {
        return expectedConsultationTime;
    }

    public void setExpectedConsultationTime(LocalTime expectedConsultationTime) {
        this.expectedConsultationTime = expectedConsultationTime;
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

    public String getConsultationRoom() {
        return consultationRoom;
    }

    public void setConsultationRoom(String consultationRoom) {
        this.consultationRoom = consultationRoom;
    }

    public int getPatientsAhead() {
        return patientsAhead;
    }

    public void setPatientsAhead(int patientsAhead) {
        this.patientsAhead = patientsAhead;
    }

    public int getAvgConsultationDuration() {
        return avgConsultationDuration;
    }

    public void setAvgConsultationDuration(int avgConsultationDuration) {
        this.avgConsultationDuration = avgConsultationDuration;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public void setEstimatedWaitMinutes(int estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.estimatedWaitFormatted = formatDuration(estimatedWaitMinutes);
    }

    public String getEstimatedWaitFormatted() {
        return estimatedWaitFormatted != null ? estimatedWaitFormatted : formatDuration(estimatedWaitMinutes);
    }

    public void setEstimatedWaitFormatted(String estimatedWaitFormatted) {
        this.estimatedWaitFormatted = estimatedWaitFormatted;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }
}
