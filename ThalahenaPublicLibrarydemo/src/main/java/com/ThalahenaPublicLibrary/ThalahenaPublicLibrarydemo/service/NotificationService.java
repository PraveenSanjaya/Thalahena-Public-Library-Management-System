package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.NotificationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Notification;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification Service - Handles all business logic for notification management
 * 
 * SRP (Single Responsibility Principle):
 * - This service ONLY handles notification business logic
 * - Does NOT handle HTTP concerns (that's the controller's job)
 * - Does NOT handle database access directly (that's the repository's job)
 * - Single purpose: create, update, delete, and retrieve notifications
 * 
 * OCP (Open/Closed Principle):
 * - Open for extension: Can add new notification types or delivery methods
 * - Closed for modification: Existing methods won't need changes for new features
 * - Future: Can add email notifications, push notifications, etc.
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on NotificationRepository abstraction, not concrete implementation
 * - Spring injects dependencies, we don't create them
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

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
     * Get notification by ID
     */
    public NotificationDTO getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        return convertToDTO(notification);
    }

    /**
     * Create a new notification (broadcast or user-specific)
     * SRP: Validates and creates notification with timestamps
     */
    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        // Validation
        if (notificationDTO.getTitle() == null || notificationDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification title cannot be empty");
        }
        if (notificationDTO.getMessage() == null || notificationDTO.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }

        Notification notification = Notification.builder()
                .title(notificationDTO.getTitle().trim())
                .message(notificationDTO.getMessage().trim())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Note: user is null for broadcast notifications
        // If user-specific, would need to fetch user from repository

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    /**
     * Update an existing notification
     * SRP: Validates and updates notification with new timestamp
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
     * Convert Notification entity to DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .userName(notification.getUser() != null ? notification.getUser().getUsername() : null)
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .isBroadcast(notification.getUser() == null)
                .build();
    }
}
