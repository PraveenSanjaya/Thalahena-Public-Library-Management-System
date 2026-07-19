package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "otp", "role", "firstName", "lastName", "birthDate",
            "gender", "membershipDate", "whatsapp", "socialMedia", "phone", "active",
            "hibernateLazyInitializer", "handler"})
    private User user;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime createdAt;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Feedback() {}

    public Feedback(Long id, User user, String message, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.createdAt = createdAt;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static FeedbackBuilder builder() { return new FeedbackBuilder(); }

    public static class FeedbackBuilder {
        private Long id;
        private User user;
        private String message;
        private LocalDateTime createdAt;

        public FeedbackBuilder id(Long id)                   { this.id = id; return this; }
        public FeedbackBuilder user(User user)               { this.user = user; return this; }
        public FeedbackBuilder message(String message)       { this.message = message; return this; }
        public FeedbackBuilder createdAt(LocalDateTime dt)   { this.createdAt = dt; return this; }

        public Feedback build() {
            Feedback f = new Feedback();
            f.id = this.id; f.user = this.user;
            f.message = this.message; f.createdAt = this.createdAt;
            return f;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                           { return id; }
    public void setId(Long id)                   { this.id = id; }
    public User getUser()                         { return user; }
    public void setUser(User user)               { this.user = user; }
    public String getMessage()                    { return message; }
    public void setMessage(String message)        { this.message = message; }
    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime dt)    { this.createdAt = dt; }
}
