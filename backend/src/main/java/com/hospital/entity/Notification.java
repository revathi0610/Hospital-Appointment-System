package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Notification Entity for In-App Alerts and Reminders.
 * Supports Feature 5: Automatic Appointment Reminders & Real-Time Alerts.
 */
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 50)
    private String type = "GENERAL"; // REMINDER, URGENT_REMINDER, CONFIRMATION, CANCELLATION, SLOT_RELEASED, WAITLIST_ALERT

    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(Long userId, String title, String message, String type) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = (type != null) ? type : "GENERAL";
        this.isRead = false;
    }

    public Notification(Long userId, String title, String message, String type, Long appointmentId) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = (type != null) ? type : "GENERAL";
        this.appointmentId = appointmentId;
        this.isRead = false;
    }

    public Notification(User user, String title, String message, String type, Long appointmentId) {
        this.userId = (user != null) ? user.getId() : null;
        this.title = title;
        this.message = message;
        this.type = (type != null) ? type : "GENERAL";
        this.appointmentId = appointmentId;
        this.isRead = false;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
