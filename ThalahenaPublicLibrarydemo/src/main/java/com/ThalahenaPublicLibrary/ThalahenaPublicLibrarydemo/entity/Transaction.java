package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Transaction Entity - Represents book borrowing and returning
 *
 * SRP: Only represents transaction data, no business logic
 */
@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_user", columnList = "user_id"),
    @Index(name = "idx_transaction_book", columnList = "book_id"),
    @Index(name = "idx_transaction_status", columnList = "status")
})
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

    private Double fineAmount = 0.0;

    @Enumerated(EnumType.STRING)
    private BookCondition bookCondition;

    @Column(columnDefinition = "TEXT")
    private String conditionNotes;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Transaction() {}

    public Transaction(Long id, User user, Book book, LocalDate issueDate, LocalDate dueDate,
                       LocalDate returnDate, TransactionStatus status, Double fineAmount,
                       BookCondition bookCondition, String conditionNotes) {
        this.id = id; this.user = user; this.book = book;
        this.issueDate = issueDate; this.dueDate = dueDate; this.returnDate = returnDate;
        this.status = status;
        this.fineAmount = fineAmount != null ? fineAmount : 0.0;
        this.bookCondition = bookCondition; this.conditionNotes = conditionNotes;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static TransactionBuilder builder() { return new TransactionBuilder(); }

    public static class TransactionBuilder {
        private Long id;
        private User user;
        private Book book;
        private LocalDate issueDate;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private TransactionStatus status;
        private Double fineAmount = 0.0;
        private BookCondition bookCondition;
        private String conditionNotes;

        public TransactionBuilder id(Long id)                     { this.id = id; return this; }
        public TransactionBuilder user(User user)                 { this.user = user; return this; }
        public TransactionBuilder book(Book book)                 { this.book = book; return this; }
        public TransactionBuilder issueDate(LocalDate d)          { this.issueDate = d; return this; }
        public TransactionBuilder dueDate(LocalDate d)            { this.dueDate = d; return this; }
        public TransactionBuilder returnDate(LocalDate d)         { this.returnDate = d; return this; }
        public TransactionBuilder status(TransactionStatus s)     { this.status = s; return this; }
        public TransactionBuilder fineAmount(Double v)            { this.fineAmount = v; return this; }
        public TransactionBuilder bookCondition(BookCondition c)  { this.bookCondition = c; return this; }
        public TransactionBuilder conditionNotes(String n)        { this.conditionNotes = n; return this; }

        public Transaction build() {
            Transaction t = new Transaction();
            t.id = this.id; t.user = this.user; t.book = this.book;
            t.issueDate = this.issueDate; t.dueDate = this.dueDate; t.returnDate = this.returnDate;
            t.status = this.status;
            t.fineAmount = this.fineAmount != null ? this.fineAmount : 0.0;
            t.bookCondition = this.bookCondition; t.conditionNotes = this.conditionNotes;
            return t;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                             { return id; }
    public void setId(Long id)                     { this.id = id; }
    public User getUser()                           { return user; }
    public void setUser(User user)                 { this.user = user; }
    public Book getBook()                           { return book; }
    public void setBook(Book book)                 { this.book = book; }
    public LocalDate getIssueDate()                { return issueDate; }
    public void setIssueDate(LocalDate d)          { this.issueDate = d; }
    public LocalDate getDueDate()                  { return dueDate; }
    public void setDueDate(LocalDate d)            { this.dueDate = d; }
    public LocalDate getReturnDate()               { return returnDate; }
    public void setReturnDate(LocalDate d)         { this.returnDate = d; }
    public TransactionStatus getStatus()           { return status; }
    public void setStatus(TransactionStatus s)     { this.status = s; }
    public Double getFineAmount()                  { return fineAmount; }
    public void setFineAmount(Double v)            { this.fineAmount = v; }
    public BookCondition getBookCondition()        { return bookCondition; }
    public void setBookCondition(BookCondition c)  { this.bookCondition = c; }
    public String getConditionNotes()              { return conditionNotes; }
    public void setConditionNotes(String n)        { this.conditionNotes = n; }
}
