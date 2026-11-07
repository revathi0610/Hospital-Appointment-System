package com.hospital.dto;

public class DoctorDashboardStatsDto {
    private long todayAppointments;
    private long upcomingAppointments;
    private long totalPatients;
    private long pendingAppointments;

    public DoctorDashboardStatsDto() {
    }

    public DoctorDashboardStatsDto(long todayAppointments, long upcomingAppointments, long totalPatients, long pendingAppointments) {
        this.todayAppointments = todayAppointments;
        this.upcomingAppointments = upcomingAppointments;
        this.totalPatients = totalPatients;
        this.pendingAppointments = pendingAppointments;
    }

    public long getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(long todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public long getUpcomingAppointments() {
        return upcomingAppointments;
    }

    public void setUpcomingAppointments(long upcomingAppointments) {
        this.upcomingAppointments = upcomingAppointments;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }
}
