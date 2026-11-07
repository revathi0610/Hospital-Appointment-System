package com.hospital.dto;

public class TimeSlotDto {
    private String time;
    private boolean available;
    private String reason;

    public TimeSlotDto() {
    }

    public TimeSlotDto(String time, boolean available, String reason) {
        this.time = time;
        this.available = available;
        this.reason = reason;
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
}
