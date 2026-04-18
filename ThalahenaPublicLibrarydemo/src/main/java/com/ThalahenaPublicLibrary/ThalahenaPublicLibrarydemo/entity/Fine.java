package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Fine Entity - Represents fines for overdue book returns
 * 
 * SRP: Only represents fine data, no business logic
 * One transaction → One fine record (OneToOne relationship)
 */
@Entity
@Table(name = "fines", indexes = {
    @Index(name = "idx_fine_transaction", columnList = "transaction_id"),
    @Index(name = "idx_fine_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private Transaction transaction;

    @Column(nullable = false)
    private Double amount;
    
    private LocalDate returnDate;
    private LocalDate paidDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FineStatus status;
}
