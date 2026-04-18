package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.TransactionDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.*;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transaction Service - Handles all business logic for book borrowing and returning
 * 
 * SRP (Single Responsibility Principle):
 * - This service ONLY handles transaction business logic
 * - Validation, fine calculation, and status updates are centralized here
 * - Does NOT handle HTTP concerns (that's the controller's job)
 * 
 * OCP (Open/Closed Principle):
 * - Can add new transaction types without modifying existing code
 * - Extension through new methods, not modification of existing ones
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on Repository abstractions, not concrete implementations
 * - Spring injects dependencies, we don't create them
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FineRepository fineRepository;
    
    @Autowired
    private FineCalculatorService fineCalculatorService;

    /**
     * Get all transactions with optional status filter
     */
    public List<TransactionDTO> getTransactions(String statusFilter) {
        List<Transaction> transactions;
        
        if (statusFilter != null && !statusFilter.isEmpty()) {
            // Update overdue statuses first
            updateOverdueTransactions();
            
            TransactionStatus status = TransactionStatus.valueOf(statusFilter.toUpperCase());
            transactions = transactionRepository.findByStatus(status);
        } else {
            // Update overdue statuses first
            updateOverdueTransactions();
            transactions = transactionRepository.findAll();
        }
        
        return transactions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get transaction counters for dashboard cards
     */
    public TransactionCountersDTO getCounters() {
        // Update overdue statuses first
        updateOverdueTransactions();
        
        Long totalBorrows = transactionRepository.countByStatus(TransactionStatus.ISSUED);
        Long totalOverdue = transactionRepository.countByStatus(TransactionStatus.OVERDUE);
        Long totalReturned = transactionRepository.countByStatus(TransactionStatus.RETURNED);
        
        return TransactionCountersDTO.builder()
                .totalBorrows(totalBorrows != null ? totalBorrows : 0L)
                .totalOverdue(totalOverdue != null ? totalOverdue : 0L)
                .totalReturned(totalReturned != null ? totalReturned : 0L)
                .build();
    }

    /**
     * Issue a book to a member
     * SRP: Validates, creates transaction, updates book quantity
     */
    @Transactional
    public TransactionDTO issueBook(Long userId, Long bookId) {
        // Find user and book
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + userId));
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        // Validation: Check book availability
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException(
                    "No copies available for book: " + book.getTitle() + 
                    ". Total copies: " + book.getTotalCopies()
            );
        }

        // Check if user already has this book issued
        boolean hasActiveTransaction = transactionRepository.findByUser(user).stream()
                .anyMatch(t -> t.getBook().getId().equals(bookId) && 
                              (t.getStatus() == TransactionStatus.ISSUED || 
                               t.getStatus() == TransactionStatus.OVERDUE));
        
        if (hasActiveTransaction) {
            throw new IllegalStateException(
                    "Member already has this book issued. Please return it first."
            );
        }

        // Decrease available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Create transaction
        Transaction transaction = Transaction.builder()
                .user(user)
                .book(book)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14)) // 14 days borrowing period
                .status(TransactionStatus.ISSUED)
                .fineAmount(0.0)
                .bookCondition(BookCondition.GOOD)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    /**
     * Return a book
     * SRP: Updates transaction, calculates fine, increases book quantity
     */
    @Transactional
    public TransactionDTO returnBook(Long transactionId, LocalDate returnDate, 
                                      BookCondition bookCondition, String conditionNotes) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // Validation: Check if already returned
        if (transaction.getStatus() == TransactionStatus.RETURNED) {
            throw new IllegalStateException("This book has already been returned.");
        }

        // Use provided return date or today
        LocalDate actualReturnDate = returnDate != null ? returnDate : LocalDate.now();

        // Update transaction
        transaction.setReturnDate(actualReturnDate);
        transaction.setStatus(TransactionStatus.RETURNED);
        
        // Update book condition if provided
        if (bookCondition != null) {
            transaction.setBookCondition(bookCondition);
        }
        if (conditionNotes != null && !conditionNotes.isEmpty()) {
            transaction.setConditionNotes(conditionNotes);
        }

        // Calculate fine using FineCalculatorService
        double fineAmount = fineCalculatorService.calculateFine(
                transaction.getDueDate(), 
                actualReturnDate
        );
        transaction.setFineAmount(fineAmount);

        // Increase available copies
        Book book = transaction.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Create fine record (always, even if amount is 0)
        Fine fine = Fine.builder()
                .transaction(transaction)
                .amount(fineAmount)
                .returnDate(actualReturnDate)
                .status(fineAmount > 0 ? FineStatus.UNPAID : FineStatus.NONE)
                .build();
        fineRepository.save(fine);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    /**
     * Update overdue transactions
     * Automatically marks transactions as OVERDUE if due date has passed
     */
    private void updateOverdueTransactions() {
        List<Transaction> issuedTransactions = transactionRepository.findByStatus(TransactionStatus.ISSUED);
        LocalDate today = LocalDate.now();
        
        for (Transaction transaction : issuedTransactions) {
            if (transaction.getDueDate() != null && transaction.getDueDate().isBefore(today)) {
                transaction.setStatus(TransactionStatus.OVERDUE);
                transactionRepository.save(transaction);
            }
        }
    }

    /**
     * Convert Transaction entity to DTO
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .memberName(transaction.getUser().getUsername())
                .memberEmail(transaction.getUser().getEmail())
                .bookId(transaction.getBook().getId())
                .bookTitle(transaction.getBook().getTitle())
                .bookIsbn(transaction.getBook().getIsbn())
                .issueDate(transaction.getIssueDate())
                .dueDate(transaction.getDueDate())
                .returnDate(transaction.getReturnDate())
                .status(transaction.getStatus().toString())
                .fineAmount(transaction.getFineAmount())
                .bookCondition(transaction.getBookCondition())
                .conditionNotes(transaction.getConditionNotes())
                .build();
    }

    /**
     * DTO for transaction counters
     */
    public static class TransactionCountersDTO {
        private Long totalBorrows;
        private Long totalOverdue;
        private Long totalReturned;

        public static TransactionCountersDTOBuilder builder() {
            return new TransactionCountersDTOBuilder();
        }

        public Long getTotalBorrows() { return totalBorrows; }
        public void setTotalBorrows(Long totalBorrows) { this.totalBorrows = totalBorrows; }
        public Long getTotalOverdue() { return totalOverdue; }
        public void setTotalOverdue(Long totalOverdue) { this.totalOverdue = totalOverdue; }
        public Long getTotalReturned() { return totalReturned; }
        public void setTotalReturned(Long totalReturned) { this.totalReturned = totalReturned; }

        public static class TransactionCountersDTOBuilder {
            private Long totalBorrows;
            private Long totalOverdue;
            private Long totalReturned;

            public TransactionCountersDTOBuilder totalBorrows(Long totalBorrows) {
                this.totalBorrows = totalBorrows;
                return this;
            }

            public TransactionCountersDTOBuilder totalOverdue(Long totalOverdue) {
                this.totalOverdue = totalOverdue;
                return this;
            }

            public TransactionCountersDTOBuilder totalReturned(Long totalReturned) {
                this.totalReturned = totalReturned;
                return this;
            }

            public TransactionCountersDTO build() {
                TransactionCountersDTO dto = new TransactionCountersDTO();
                dto.setTotalBorrows(this.totalBorrows);
                dto.setTotalOverdue(this.totalOverdue);
                dto.setTotalReturned(this.totalReturned);
                return dto;
            }
        }
    }
}
