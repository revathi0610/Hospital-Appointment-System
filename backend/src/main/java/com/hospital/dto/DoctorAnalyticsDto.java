package com.hospital.dto;

import java.util.Map;

public class DoctorAnalyticsDto {
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private long totalAppointments;
    private long todayAppointments;
    private long thisWeekAppointments;
    private long thisMonthAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long pendingAppointments;
    private long confirmedAppointments;
    private double completionRate;
    private double cancellationRate;
    private int avgConsultationDuration;
    private Map<String, Long> statusBreakdown;
    private Map<String, Long> dayOfWeekDistribution;
    private Map<String, Long> timeSlotDistribution;

    public DoctorAnalyticsDto() {
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

    public long getThisWeekAppointments() {
        return thisWeekAppointments;
    }

    public void setThisWeekAppointments(long thisWeekAppointments) {
        this.thisWeekAppointments = thisWeekAppointments;
    }

    public long getThisMonthAppointments() {
        return thisMonthAppointments;
    }

    public void setThisMonthAppointments(long thisMonthAppointments) {
        this.thisMonthAppointments = thisMonthAppointments;
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

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(double cancellationRate) {
        this.cancellationRate = cancellationRate;
    }

    public int getAvgConsultationDuration() {
        return avgConsultationDuration;
    }

    public void setAvgConsultationDuration(int avgConsultationDuration) {
        this.avgConsultationDuration = avgConsultationDuration;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public Map<String, Long> getDayOfWeekDistribution() {
        return dayOfWeekDistribution;
    }

    public void setDayOfWeekDistribution(Map<String, Long> dayOfWeekDistribution) {
        this.dayOfWeekDistribution = dayOfWeekDistribution;
    }

    public Map<String, Long> getTimeSlotDistribution() {
        return timeSlotDistribution;
    }

    public void setTimeSlotDistribution(Map<String, Long> timeSlotDistribution) {
        this.timeSlotDistribution = timeSlotDistribution;
    }
}
