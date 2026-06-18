package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.NotificationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Notification;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.NotificationRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification Service - Handles all business logic for notification management
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all notifications (for staff view)
     */
    public List<NotificationDTO> getAllNotifications(String search) {
        List<Notification> notifications;
        
        if (search != null && !search.isEmpty()) {
            notifications = notificationRepository.searchByTitleOrMessage(search);
        } else {
            notifications = notificationRepository.findAllByOrderByCreatedAtDesc();
        }
        
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get notifications for a specific member/user (user-specific + broadcast)
     */
    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findMemberNotifications(userId);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get notification by ID
     */
    public NotificationDTO getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        return convertToDTO(notification);
    }

    /**
     * Create a new notification (broadcast or user-specific)
     */
    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        // Validation
        if (notificationDTO.getMessage() == null || notificationDTO.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }

        String type = notificationDTO.getType();
        if (type == null || type.trim().isEmpty()) {
            type = "GENERAL";
        }

        String title = notificationDTO.getTitle();
        if (title == null || title.trim().isEmpty()) {
            title = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase().replace('_', ' ') + " Notification";
        }

        User user = null;
        if (notificationDTO.getUserId() != null) {
            user = userRepository.findById(notificationDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + notificationDTO.getUserId()));
        }

        Notification notification = Notification.builder()
                .title(title.trim())
                .message(notificationDTO.getMessage().trim())
                .type(type)
                .user(user)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    /**
     * Update an existing notification
     */
    @Transactional
    public NotificationDTO updateNotification(Long notificationId, NotificationDTO notificationDTO) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

        // Validation
        if (notificationDTO.getTitle() != null && !notificationDTO.getTitle().trim().isEmpty()) {
            notification.setTitle(notificationDTO.getTitle().trim());
        }
        if (notificationDTO.getMessage() != null && !notificationDTO.getMessage().trim().isEmpty()) {
            notification.setMessage(notificationDTO.getMessage().trim());
        }
        if (notificationDTO.getType() != null && !notificationDTO.getType().trim().isEmpty()) {
            notification.setType(notificationDTO.getType().trim());
        }
        if (notificationDTO.getUserId() != null) {
            User user = userRepository.findById(notificationDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + notificationDTO.getUserId()));
            notification.setUser(user);
        } else if (notificationDTO.getIsBroadcast() != null && notificationDTO.getIsBroadcast()) {
            notification.setUser(null);
        }

        notification.setUpdatedAt(LocalDateTime.now());

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    /**
     * Delete a notification
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    /**
     * Mark a notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        notification.setIsRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * Mark a notification as read (validated for a specific member)
     */
    @Transactional
    public void markAsReadForUser(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        if (notification.getUser() != null && !notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Access denied: You cannot modify this notification");
        }
        notification.setIsRead(true);
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * Mark all notifications as read
     */
    @Transactional
    public void markAllAsRead() {
        List<Notification> notifications = notificationRepository.findAll();
        for (Notification notification : notifications) {
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notification.setUpdatedAt(LocalDateTime.now());
            }
        }
        notificationRepository.saveAll(notifications);
    }

    /**
     * Mark all notifications as read for a specific member
     */
    @Transactional
    public void markAllAsReadForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findMemberNotifications(userId);
        for (Notification notification : notifications) {
            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notification.setUpdatedAt(LocalDateTime.now());
            }
        }
        notificationRepository.saveAll(notifications);
    }

    /**
     * Convert Notification entity to DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .userName(notification.getUser() != null ? notification.getUser().getUsername() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .isBroadcast(notification.getUser() == null)
                .build();
    }
}
