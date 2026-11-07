package com.hospital.dto;

import jakarta.validation.constraints.NotBlank;

public class AppointmentStatusUpdateRequest {

    @NotBlank(message = "Status cannot be blank")
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED

    public AppointmentStatusUpdateRequest() {
    }

    public AppointmentStatusUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
