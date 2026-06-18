package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.NotificationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

/**
 * Notification Controller - Handles HTTP requests for notification management
 * 
 * SRP: Only handles HTTP request/response, delegates to NotificationService
 * OCP: Can add new endpoints without modifying existing ones
 * DIP: Depends on NotificationService abstraction
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * GET /api/notifications?search=...
     * Get all notifications (or member-specific notifications if user has MEMBER role)
     */
    @GetMapping
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(
            @RequestParam(required = false) String search) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            boolean isMember = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
            if (isMember) {
                List<NotificationDTO> notifications = notificationService.getNotificationsForUser(userDetails.getId());
                return ResponseEntity.ok(notifications);
            }
        }
        List<NotificationDTO> notifications = notificationService.getAllNotifications(search);
        return ResponseEntity.ok(notifications);
    }

    /**
     * GET /api/notifications/{id}
     * Get single notification (with member validation)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getNotificationById(@PathVariable Long id) {
        try {
            NotificationDTO notification = notificationService.getNotificationById(id);
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                boolean isMember = userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
                if (isMember && notification.getUserId() != null && !notification.getUserId().equals(userDetails.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new MessageResponse("Access denied: This notification does not belong to you"));
                }
            }
            return ResponseEntity.ok(notification);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * POST /api/notifications
     * Create a new notification (broadcast to all members)
     */
    @PostMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
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
     * PUT /api/notifications/{id}
     * Update an existing notification
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
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
     * PUT /api/notifications/{id}/read
     * Mark a notification as read (with member validation)
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                boolean isMember = userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
                if (isMember) {
                    notificationService.markAsReadForUser(id, userDetails.getId());
                } else {
                    notificationService.markAsRead(id);
                }
                return ResponseEntity.ok(new MessageResponse("Notification marked as read"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * PUT /api/notifications/read-all
     * Mark all notifications as read (for all if staff/admin, only user's if member)
     */
    @PutMapping("/read-all")
    @PreAuthorize("hasRole('MEMBER') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> markAllAsRead() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                boolean isMember = userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
                if (isMember) {
                    notificationService.markAllAsReadForUser(userDetails.getId());
                } else {
                    notificationService.markAllAsRead();
                }
                return ResponseEntity.ok(new MessageResponse("All notifications marked as read"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error marking notifications as read: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/notifications/{id}
     * Delete a notification
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
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
