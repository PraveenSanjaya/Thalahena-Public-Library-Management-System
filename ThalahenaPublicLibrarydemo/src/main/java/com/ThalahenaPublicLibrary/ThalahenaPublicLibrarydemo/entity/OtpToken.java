package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens")
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private boolean used = false;

    // ── Constructors ─────────────────────────────────────────────────────────
    public OtpToken() {}

    public OtpToken(Long id, String email, String otp, LocalDateTime expiryTime, boolean used) {
        this.id = id; this.email = email; this.otp = otp;
        this.expiryTime = expiryTime; this.used = used;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static OtpTokenBuilder builder() { return new OtpTokenBuilder(); }

    public static class OtpTokenBuilder {
        private Long id;
        private String email;
        private String otp;
        private LocalDateTime expiryTime;
        private boolean used = false;

        public OtpTokenBuilder id(Long id)                 { this.id = id; return this; }
        public OtpTokenBuilder email(String email)         { this.email = email; return this; }
        public OtpTokenBuilder otp(String otp)             { this.otp = otp; return this; }
        public OtpTokenBuilder expiryTime(LocalDateTime t) { this.expiryTime = t; return this; }
        public OtpTokenBuilder used(boolean used)          { this.used = used; return this; }

        public OtpToken build() {
            OtpToken o = new OtpToken();
            o.id = this.id; o.email = this.email; o.otp = this.otp;
            o.expiryTime = this.expiryTime; o.used = this.used;
            return o;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                         { return id; }
    public void setId(Long id)                 { this.id = id; }
    public String getEmail()                    { return email; }
    public void setEmail(String email)          { this.email = email; }
    public String getOtp()                      { return otp; }
    public void setOtp(String otp)             { this.otp = otp; }
    public LocalDateTime getExpiryTime()        { return expiryTime; }
    public void setExpiryTime(LocalDateTime t)  { this.expiryTime = t; }
    public boolean isUsed()                     { return used; }
    public void setUsed(boolean used)           { this.used = used; }
}
