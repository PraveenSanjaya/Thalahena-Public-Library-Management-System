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
 * - Can add new fields (e.g., expiryDate) without modifying existing ones
 * - ReservationStatus enum is extensible for new statuses
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on User and Book abstractions via @ManyToOne
 */
@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_user", columnList = "user_id"),
    @Index(name = "idx_reservation_book", columnList = "book_id"),
    @Index(name = "idx_reservation_status", columnList = "status"),
    @Index(name = "idx_reservation_processed", columnList = "processed")
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

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    
    // Staff acknowledgment flag (OCP: extensible for more workflow states)
    @Builder.Default
    private Boolean processed = false;
}
