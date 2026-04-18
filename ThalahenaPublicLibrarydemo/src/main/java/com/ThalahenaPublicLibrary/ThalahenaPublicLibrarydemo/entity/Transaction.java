package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Transaction Entity - Represents book borrowing and returning
 * 
 * SRP (Single Responsibility Principle):
 * - Only represents transaction data, no business logic
 * - Book condition tracking is part of transaction responsibility
 * 
 * OCP (Open/Closed Principle):
 * - Can add new fields (e.g., renewal count) without modifying existing ones
 * - BookCondition enum is extensible for new condition types
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_user", columnList = "user_id"),
    @Index(name = "idx_transaction_book", columnList = "book_id"),
    @Index(name = "idx_transaction_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Builder.Default
    private Double fineAmount = 0.0;

    // Book condition tracking (OCP: extensible via BookCondition enum)
    @Enumerated(EnumType.STRING)
    private BookCondition bookCondition;
    
    @Column(columnDefinition = "TEXT")
    private String conditionNotes;
}
