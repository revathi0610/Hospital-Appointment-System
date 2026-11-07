package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.NotificationDto;
import com.hospital.service.AppointmentReminderScheduler;
import com.hospital.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final AppointmentReminderScheduler reminderScheduler;

    public NotificationController(NotificationService notificationService,
                                  AppointmentReminderScheduler reminderScheduler) {
        this.notificationService = notificationService;
        this.reminderScheduler = reminderScheduler;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getNotificationsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", notifications));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(@PathVariable Long userId) {
        List<NotificationDto> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread notifications retrieved", notifications));
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }

    @PostMapping("/trigger-reminders")
    public ResponseEntity<ApiResponse<Integer>> triggerReminders() {
        int count = reminderScheduler.triggerReminderScan();
        return ResponseEntity.ok(ApiResponse.success("Reminder scan completed. Sent " + count + " reminders.", count));
    }
}
