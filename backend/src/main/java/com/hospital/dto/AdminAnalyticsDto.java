package com.hospital.dto;

import java.util.Map;

public class AdminAnalyticsDto {
    private long totalAppointments;
    private long totalPatients;
    private long totalDoctors;
    private long completedAppointments;
    private long cancelledAppointments;
    private long pendingAppointments;
    private long confirmedAppointments;
    private long releasedAppointments;
    private double overallCompletionRate;
    private double overallCancellationRate;
    private double averageHospitalWaitTimeMinutes;
    private Map<String, Long> appointmentsByDepartment;
    private Map<String, Long> statusBreakdown;
    private Map<String, Long> dailyVolumeRecentDays;
    private double capacityUtilizationPercentage;

    public AdminAnalyticsDto() {
    }

    public long getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(long totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getTotalDoctors() {
        return totalDoctors;
    }

    public void setTotalDoctors(long totalDoctors) {
        this.totalDoctors = totalDoctors;
    }

    public long getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(long cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }

    public long getConfirmedAppointments() {
        return confirmedAppointments;
    }

    public void setConfirmedAppointments(long confirmedAppointments) {
        this.confirmedAppointments = confirmedAppointments;
    }

    public long getReleasedAppointments() {
        return releasedAppointments;
    }

    public void setReleasedAppointments(long releasedAppointments) {
        this.releasedAppointments = releasedAppointments;
    }

    public double getOverallCompletionRate() {
        return overallCompletionRate;
    }

    public void setOverallCompletionRate(double overallCompletionRate) {
        this.overallCompletionRate = overallCompletionRate;
    }

    public double getOverallCancellationRate() {
        return overallCancellationRate;
    }

    public void setOverallCancellationRate(double overallCancellationRate) {
        this.overallCancellationRate = overallCancellationRate;
    }

    public double getAverageHospitalWaitTimeMinutes() {
        return averageHospitalWaitTimeMinutes;
    }

    public void setAverageHospitalWaitTimeMinutes(double averageHospitalWaitTimeMinutes) {
        this.averageHospitalWaitTimeMinutes = averageHospitalWaitTimeMinutes;
    }

    public Map<String, Long> getAppointmentsByDepartment() {
        return appointmentsByDepartment;
    }

    public void setAppointmentsByDepartment(Map<String, Long> appointmentsByDepartment) {
        this.appointmentsByDepartment = appointmentsByDepartment;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public Map<String, Long> getDailyVolumeRecentDays() {
        return dailyVolumeRecentDays;
    }

    public void setDailyVolumeRecentDays(Map<String, Long> dailyVolumeRecentDays) {
        this.dailyVolumeRecentDays = dailyVolumeRecentDays;
    }

    public double getCapacityUtilizationPercentage() {
        return capacityUtilizationPercentage;
    }

    public void setCapacityUtilizationPercentage(double capacityUtilizationPercentage) {
        this.capacityUtilizationPercentage = capacityUtilizationPercentage;
    }
}
