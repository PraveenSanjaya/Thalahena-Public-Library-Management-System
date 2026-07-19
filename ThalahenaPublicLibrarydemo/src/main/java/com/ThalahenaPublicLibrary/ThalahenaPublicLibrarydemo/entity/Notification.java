package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
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
    private String type = "GENERAL";

    private Boolean isRead = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Notification() {}

    public Notification(Long id, User user, String title, String message,
                        String type, Boolean isRead, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id; this.user = user; this.title = title; this.message = message;
        this.type = type != null ? type : "GENERAL";
        this.isRead = isRead != null ? isRead : false;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static NotificationBuilder builder() { return new NotificationBuilder(); }

    public static class NotificationBuilder {
        private Long id;
        private User user;
        private String title;
        private String message;
        private String type = "GENERAL";
        private Boolean isRead = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public NotificationBuilder id(Long id)                 { this.id = id; return this; }
        public NotificationBuilder user(User user)             { this.user = user; return this; }
        public NotificationBuilder title(String title)         { this.title = title; return this; }
        public NotificationBuilder message(String message)     { this.message = message; return this; }
        public NotificationBuilder type(String type)           { this.type = type; return this; }
        public NotificationBuilder isRead(Boolean isRead)      { this.isRead = isRead; return this; }
        public NotificationBuilder createdAt(LocalDateTime dt) { this.createdAt = dt; return this; }
        public NotificationBuilder updatedAt(LocalDateTime dt) { this.updatedAt = dt; return this; }

        public Notification build() {
            Notification n = new Notification();
            n.id = this.id; n.user = this.user; n.title = this.title; n.message = this.message;
            n.type = this.type != null ? this.type : "GENERAL";
            n.isRead = this.isRead != null ? this.isRead : false;
            n.createdAt = this.createdAt; n.updatedAt = this.updatedAt;
            return n;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                           { return id; }
    public void setId(Long id)                   { this.id = id; }
    public User getUser()                         { return user; }
    public void setUser(User user)               { this.user = user; }
    public String getTitle()                      { return title; }
    public void setTitle(String title)            { this.title = title; }
    public String getMessage()                    { return message; }
    public void setMessage(String message)        { this.message = message; }
    public String getType()                       { return type; }
    public void setType(String type)             { this.type = type; }
    public Boolean getIsRead()                    { return isRead; }
    public void setIsRead(Boolean isRead)         { this.isRead = isRead; }
    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)    { this.createdAt = dt; }
    public LocalDateTime getUpdatedAt()           { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)    { this.updatedAt = dt; }
}
