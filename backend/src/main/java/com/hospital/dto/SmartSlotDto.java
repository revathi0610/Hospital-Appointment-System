package com.hospital.dto;

public class SmartSlotDto {
    private String time;
    private boolean available;
    private String reason;
    private int estimatedWaitMinutes;
    private int patientsAhead;
    private boolean recommended;
    private String recommendationBadge; // e.g., "⭐️ Shortest Wait", "Low Wait", "Moderate Wait"
    private String badgeClass; // success, info, warning, danger, light
    private boolean released;
    private Long releasedAppointmentId;

    public SmartSlotDto() {
    }

    public SmartSlotDto(String time, boolean available, String reason, int estimatedWaitMinutes, int patientsAhead, boolean recommended, String recommendationBadge) {
        this.time = time;
        this.available = available;
        this.reason = reason;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.patientsAhead = patientsAhead;
        this.recommended = recommended;
        this.recommendationBadge = recommendationBadge;
    }

    public SmartSlotDto(String time, boolean available, String reason, int estimatedWaitMinutes, String badgeClass, boolean released, Long releasedAppointmentId) {
        this.time = time;
        this.available = available;
        this.reason = reason;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
        this.badgeClass = badgeClass;
        this.recommendationBadge = reason;
        this.recommended = "success".equals(badgeClass) || "info".equals(badgeClass);
        this.released = released;
        this.releasedAppointmentId = releasedAppointmentId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getEstimatedWaitMinutes() {
        return estimatedWaitMinutes;
    }

    public void setEstimatedWaitMinutes(int estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public int getPatientsAhead() {
        return patientsAhead;
    }

    public void setPatientsAhead(int patientsAhead) {
        this.patientsAhead = patientsAhead;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public String getRecommendationBadge() {
        return recommendationBadge;
    }

    public void setRecommendationBadge(String recommendationBadge) {
        this.recommendationBadge = recommendationBadge;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public void setBadgeClass(String badgeClass) {
        this.badgeClass = badgeClass;
    }

    public boolean isReleased() {
        return released;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public Long getReleasedAppointmentId() {
        return releasedAppointmentId;
    }

    public void setReleasedAppointmentId(Long releasedAppointmentId) {
        this.releasedAppointmentId = releasedAppointmentId;
    }
}
