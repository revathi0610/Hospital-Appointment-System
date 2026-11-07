package com.hospital.dto;

import java.util.List;

public class DashboardStatsDto {
    private long totalAppointments;
    private long todayAppointments;
    private long totalPatients;
    private long doctorsAvailable;
    private long totalDoctors;
    private List<?> upcomingAppointments;
    private List<PatientDto> recentPatients;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(long totalAppointments, long todayAppointments, long totalPatients, long doctorsAvailable, long totalDoctors) {
        this.totalAppointments = totalAppointments;
        this.todayAppointments = todayAppointments;
        this.totalPatients = totalPatients;
        this.doctorsAvailable = doctorsAvailable;
        this.totalDoctors = totalDoctors;
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(long todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getDoctorsAvailable() {
        return doctorsAvailable;
    }

    public void setDoctorsAvailable(long doctorsAvailable) {
        this.doctorsAvailable = doctorsAvailable;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public List<?> getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(List<?> upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }

    public List<PatientDto> getRecentPatients() {
        return recentPatients;
    }

    public void setRecentPatients(List<PatientDto> recentPatients) {
        this.recentPatients = recentPatients;
    }
}
