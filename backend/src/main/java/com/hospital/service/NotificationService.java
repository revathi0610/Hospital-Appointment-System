package com.hospital.service;

import com.hospital.dto.NotificationDto;
import com.hospital.entity.Notification;
import com.hospital.entity.User;
import com.hospital.repository.NotificationRepository;
import com.hospital.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification createNotification(User user, String title, String message, String type, Long appointmentId) {
        Notification notification = new Notification(user, title, message, type, appointmentId);
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification createNotification(Long userId, String title, String message, String type, Long appointmentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return createNotification(user, title, message, type, appointmentId);
    }

    public List<NotificationDto> getNotificationsForUser(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotificationsForUser(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));
        if (notification.getUserId() != null && notification.getUserId().equals(userId)) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }

    public boolean hasRecentNotification(Long userId, String title, LocalDateTime after) {
        return notificationRepository.existsByUserIdAndTitleAndCreatedAtAfter(userId, title, after);
    }

    private NotificationDto mapToDto(Notification n) {
        NotificationDto dto = new NotificationDto();
        dto.setId(n.getId());
        dto.setUserId(n.getUserId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setAppointmentId(n.getAppointmentId());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
