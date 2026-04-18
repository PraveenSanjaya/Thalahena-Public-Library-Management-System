package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Notification Entity - Represents system notifications/broadcasts
 * 
 * SRP (Single Responsibility Principle):
 * - Only represents notification data, no business logic
 * - Supports both user-specific and broadcast notifications
 * 
 * OCP (Open/Closed Principle):
 * - Can add new fields (e.g., priority, category) without modifying existing ones
 * - Extensible for different notification types
 * 
 * DIP (Dependency Inversion Principle):
 * - Optional dependency on User (nullable for broadcasts)
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

    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;
    
    @Builder.Default
    private Boolean isRead = false;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
