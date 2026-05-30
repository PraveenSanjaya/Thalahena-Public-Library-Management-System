package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Notification Entity - Represents system notifications/broadcasts
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "user_id"),
    @Index(name = "idx_notification_is_read", columnList = "is_read"),
    @Index(name = "idx_notification_created", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Optional: null means broadcast to all members
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // Notification type: GENERAL, DUE_DATE, OVERDUE, RESERVATION
    @Builder.Default
    private String type = "GENERAL";
    
    @Builder.Default
    private Boolean isRead = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
