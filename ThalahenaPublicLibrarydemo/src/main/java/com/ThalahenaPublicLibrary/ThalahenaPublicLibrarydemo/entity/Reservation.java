package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Reservation Entity - Represents member book reservations
 *
 * SRP: Only represents reservation data, no business logic.
 * OCP: Extensible with new fields/statuses.
 */
@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_user", columnList = "user_id"),
    @Index(name = "idx_reservation_book", columnList = "book_id"),
    @Index(name = "idx_reservation_status", columnList = "status"),
    @Index(name = "idx_reservation_processed", columnList = "processed"),
    @Index(name = "idx_reservation_expiry", columnList = "expiry_date")
})
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDateTime reservationDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private Boolean processed = false;

    /**
     * Automatically set expiryDate = reservationDate + 3 days before persisting
     */
    @PrePersist
    public void prePersist() {
        if (this.expiryDate == null && this.reservationDate != null) {
            this.expiryDate = this.reservationDate.plusDays(3);
        }
    }

    // ── Constructors ─────────────────────────────────────────────────────────
    public Reservation() {}

    public Reservation(Long id, User user, Book book, LocalDateTime reservationDate,
                       LocalDateTime expiryDate, ReservationStatus status, Boolean processed) {
        this.id = id; this.user = user; this.book = book;
        this.reservationDate = reservationDate; this.expiryDate = expiryDate;
        this.status = status; this.processed = processed != null ? processed : false;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static ReservationBuilder builder() { return new ReservationBuilder(); }

    public static class ReservationBuilder {
        private Long id;
        private User user;
        private Book book;
        private LocalDateTime reservationDate;
        private LocalDateTime expiryDate;
        private ReservationStatus status;
        private Boolean processed = false;

        public ReservationBuilder id(Long id)                         { this.id = id; return this; }
        public ReservationBuilder user(User user)                     { this.user = user; return this; }
        public ReservationBuilder book(Book book)                     { this.book = book; return this; }
        public ReservationBuilder reservationDate(LocalDateTime dt)   { this.reservationDate = dt; return this; }
        public ReservationBuilder expiryDate(LocalDateTime dt)        { this.expiryDate = dt; return this; }
        public ReservationBuilder status(ReservationStatus s)         { this.status = s; return this; }
        public ReservationBuilder processed(Boolean processed)        { this.processed = processed; return this; }

        public Reservation build() {
            Reservation r = new Reservation();
            r.id = this.id; r.user = this.user; r.book = this.book;
            r.reservationDate = this.reservationDate; r.expiryDate = this.expiryDate;
            r.status = this.status; r.processed = this.processed != null ? this.processed : false;
            return r;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                               { return id; }
    public void setId(Long id)                       { this.id = id; }
    public User getUser()                             { return user; }
    public void setUser(User user)                   { this.user = user; }
    public Book getBook()                             { return book; }
    public void setBook(Book book)                   { this.book = book; }
    public LocalDateTime getReservationDate()         { return reservationDate; }
    public void setReservationDate(LocalDateTime dt)  { this.reservationDate = dt; }
    public LocalDateTime getExpiryDate()              { return expiryDate; }
    public void setExpiryDate(LocalDateTime dt)       { this.expiryDate = dt; }
    public ReservationStatus getStatus()              { return status; }
    public void setStatus(ReservationStatus s)        { this.status = s; }
    public Boolean getProcessed()                     { return processed; }
    public void setProcessed(Boolean processed)       { this.processed = processed; }
}
