package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "about_statements")
public class About {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime updatedAt;

    // ── Constructors ─────────────────────────────────────────────────────────
    public About() {}

    public About(Long id, String content, LocalDateTime updatedAt) {
        this.id = id; this.content = content; this.updatedAt = updatedAt;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static AboutBuilder builder() { return new AboutBuilder(); }

    public static class AboutBuilder {
        private Long id;
        private String content;
        private LocalDateTime updatedAt;

        public AboutBuilder id(Long id)                 { this.id = id; return this; }
        public AboutBuilder content(String content)     { this.content = content; return this; }
        public AboutBuilder updatedAt(LocalDateTime dt) { this.updatedAt = dt; return this; }

        public About build() {
            About a = new About();
            a.id = this.id; a.content = this.content; a.updatedAt = this.updatedAt;
            return a;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                         { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getContent()                  { return content; }
    public void setContent(String content)      { this.content = content; }
    public LocalDateTime getUpdatedAt()         { return updatedAt; }
    public void setUpdatedAt(LocalDateTime dt)  { this.updatedAt = dt; }
}
