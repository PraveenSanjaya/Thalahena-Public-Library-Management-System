package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.NotificationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notification Controller - Handles HTTP requests for notification management
 * 
 * SRP: Only handles HTTP request/response, delegates to NotificationService
 * OCP: Can add new endpoints without modifying existing ones
 * DIP: Depends on NotificationService abstraction
 */
@RestController
@RequestMapping("/api/staff/notifications")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/staff/notifications?search=...
     * Get all notifications with optional search
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(
            @RequestParam(required = false) String search) {
        List<NotificationDTO> notifications = notificationService.getAllNotifications(search);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/staff/notifications/{id}
     * Get single notification
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        try {
            NotificationDTO notification = notificationService.getNotificationById(id);
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/staff/notifications
     * Create a new notification (broadcast to all members)
     */
    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody NotificationDTO notificationDTO) {
        try {
            NotificationDTO created = notificationService.createNotification(notificationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error creating notification: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/notifications/{id}
     * Update an existing notification
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(
            @PathVariable Long id,
            @RequestBody NotificationDTO notificationDTO) {
        try {
            NotificationDTO updated = notificationService.updateNotification(id, notificationDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating notification: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/staff/notifications/{id}
     * Delete a notification
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.ok(new MessageResponse("Notification deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error deleting notification: " + e.getMessage()));
        }
    }
}
