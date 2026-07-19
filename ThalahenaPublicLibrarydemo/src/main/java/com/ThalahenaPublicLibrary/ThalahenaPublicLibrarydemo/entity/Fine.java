package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
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

    // ── Constructors ─────────────────────────────────────────────────────────
    public Fine() {}

    public Fine(Long id, Transaction transaction, Double amount,
                LocalDate returnDate, LocalDate paidDate, FineStatus status) {
        this.id = id;
        this.transaction = transaction;
        this.amount = amount;
        this.returnDate = returnDate;
        this.paidDate = paidDate;
        this.status = status;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static FineBuilder builder() { return new FineBuilder(); }

    public static class FineBuilder {
        private Long id;
        private Transaction transaction;
        private Double amount;
        private LocalDate returnDate;
        private LocalDate paidDate;
        private FineStatus status;

        public FineBuilder id(Long id)                       { this.id = id; return this; }
        public FineBuilder transaction(Transaction t)        { this.transaction = t; return this; }
        public FineBuilder amount(Double amount)             { this.amount = amount; return this; }
        public FineBuilder returnDate(LocalDate d)           { this.returnDate = d; return this; }
        public FineBuilder paidDate(LocalDate d)             { this.paidDate = d; return this; }
        public FineBuilder status(FineStatus status)         { this.status = status; return this; }

        public Fine build() {
            Fine f = new Fine();
            f.id = this.id; f.transaction = this.transaction; f.amount = this.amount;
            f.returnDate = this.returnDate; f.paidDate = this.paidDate; f.status = this.status;
            return f;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                         { return id; }
    public void setId(Long id)                 { this.id = id; }
    public Transaction getTransaction()         { return transaction; }
    public void setTransaction(Transaction t)  { this.transaction = t; }
    public Double getAmount()                   { return amount; }
    public void setAmount(Double amount)        { this.amount = amount; }
    public LocalDate getReturnDate()            { return returnDate; }
    public void setReturnDate(LocalDate d)      { this.returnDate = d; }
    public LocalDate getPaidDate()              { return paidDate; }
    public void setPaidDate(LocalDate d)        { this.paidDate = d; }
    public FineStatus getStatus()               { return status; }
    public void setStatus(FineStatus status)    { this.status = status; }
}
