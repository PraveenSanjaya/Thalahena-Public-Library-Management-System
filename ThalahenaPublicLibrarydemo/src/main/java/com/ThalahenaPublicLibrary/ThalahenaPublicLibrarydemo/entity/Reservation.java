package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Reservation Entity - Represents member book reservations
 * 
 * SRP (Single Responsibility Principle):
 * - Only represents reservation data, no business logic
 * - Processed flag tracks staff acknowledgment
 * 
 * OCP (Open/Closed Principle):
 * - Can add new fields (e.g., priority) without modifying existing ones
 * - ReservationStatus enum is extensible for new statuses
 * - expiryDate enables automatic cleanup of stale reservations
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on User and Book abstractions via @ManyToOne
 */
@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_user", columnList = "user_id"),
    @Index(name = "idx_reservation_book", columnList = "book_id"),
    @Index(name = "idx_reservation_status", columnList = "status"),
    @Index(name = "idx_reservation_processed", columnList = "processed"),
    @Index(name = "idx_reservation_expiry", columnList = "expiry_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    // Expiry date: reservation auto-cancels after this date (OCP: configurable via scheduled task)
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    
    // Staff acknowledgment flag (OCP: extensible for more workflow states)
    @Builder.Default
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
}
