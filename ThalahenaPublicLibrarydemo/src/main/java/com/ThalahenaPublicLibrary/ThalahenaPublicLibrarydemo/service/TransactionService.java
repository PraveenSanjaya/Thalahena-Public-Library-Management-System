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
     * OCP: Can add new validation rules without modifying existing logic
     * DIP: Depends on Repository abstractions, not concrete implementations
     * 
     * LIBRARY BUSINESS RULE ENFORCEMENT:
     * - One member can borrow ONLY ONE book at a time
     * - Active statuses: ISSUED, OVERDUE (prevent new borrow)
     * - Inactive status: RETURNED (allows new borrow)
     */
    @Transactional
    public TransactionDTO issueBook(Long userId, Long bookId) {
        // Step 1: Verify member exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Member not found with ID: " + userId));
        
        // Step 2: Verify book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));

        // Step 3: Check available quantity > 0 (BUSINESS RULE 3)
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Book is currently unavailable.");
        }

        // Step 4: STRICT VALIDATION - Check member has NO active borrowed/overdue book (BUSINESS RULE 1)
        // A member can have ONLY ONE active borrowed book at a time (Library Policy)
        // Optimized query: Directly fetch active transactions instead of loading all
        List<Transaction> activeTransactions = transactionRepository.findActiveTransactionsByUser(user);
        
        if (!activeTransactions.isEmpty()) {
            // Get the active transaction details for better error message
            Transaction activeTransaction = activeTransactions.get(0);
            String activeBookTitle = activeTransaction.getBook().getTitle();
            
            throw new IllegalStateException(
                    "According to library borrowing rules, a member can borrow only one book at a time. " +
                    "This member currently has '" + activeBookTitle + "' (Status: " + 
                    activeTransaction.getStatus() + "). " +
                    "Please return the currently borrowed book before borrowing another."
            );
        }

        // Step 5: Reduce available quantity by 1 (BUSINESS RULE 4)
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Step 6: Create transaction
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
     * OCP: Can add new return logic without modifying existing code
     * DIP: Depends on FineCalculatorService abstraction
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
        
        // Update book condition (mandatory)
        if (bookCondition != null) {
            transaction.setBookCondition(bookCondition);
        } else {
            throw new IllegalArgumentException("Book Condition is required");
        }
        
        // Update condition notes if provided
        if (conditionNotes != null && !conditionNotes.isEmpty()) {
            transaction.setConditionNotes(conditionNotes);
        }

        // Calculate fine using FineCalculatorService (ensures fine is never negative)
        double fineAmount = fineCalculatorService.calculateFine(
                transaction.getDueDate(), 
                actualReturnDate
        );
        
        // Validation: Fine must never be negative
        if (fineAmount < 0) {
            fineAmount = 0.0;
        }
        
        transaction.setFineAmount(fineAmount);

        // Increase available copies
        Book book = transaction.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Create fine record (always, even if amount is 0)
        // OCP: Fine entity can be extended with new statuses without modifying this code
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
     * Update transaction status and/or book condition
     * Allows manual editing of status and condition fields
     * 
     * CRITICAL: This method must enforce the "one book per member" rule when changing status to ISSUED
     */
    @Transactional
    public TransactionDTO updateTransaction(Long transactionId, String status, 
                                             String bookCondition, String conditionNotes) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // Update status if provided
        if (status != null && !status.isEmpty()) {
            try {
                TransactionStatus newStatus = TransactionStatus.valueOf(status.toUpperCase());
                
                // ENFORCE BUSINESS RULE: If changing to ISSUED, check if member already has active book
                if (newStatus == TransactionStatus.ISSUED) {
                    List<Transaction> activeTransactions = transactionRepository
                            .findActiveTransactionsByUser(transaction.getUser());
                    
                    // Filter out current transaction (in case it's being re-issued)
                    long otherActiveCount = activeTransactions.stream()
                            .filter(t -> !t.getId().equals(transactionId))
                            .count();
                    
                    if (otherActiveCount > 0) {
                        Transaction otherActive = activeTransactions.stream()
                                .filter(t -> !t.getId().equals(transactionId))
                                .findFirst().get();
                        
                        throw new IllegalStateException(
                                "Cannot issue this book. Member already has '" + 
                                otherActive.getBook().getTitle() + "' (Status: " + 
                                otherActive.getStatus() + "). " +
                                "According to library rules, a member can borrow only one book at a time."
                        );
                    }
                }
                
                transaction.setStatus(newStatus);
                
                // If status is changed to RETURNED, set return date to today if not already set
                if (newStatus == TransactionStatus.RETURNED && transaction.getReturnDate() == null) {
                    transaction.setReturnDate(LocalDate.now());
                    
                    // Increase available copies
                    Book book = transaction.getBook();
                    book.setAvailableCopies(book.getAvailableCopies() + 1);
                    bookRepository.save(book);
                }
                
                // If status is changed from RETURNED to something else, decrease available copies
                if (transaction.getReturnDate() != null && newStatus != TransactionStatus.RETURNED) {
                    transaction.setReturnDate(null);
                    Book book = transaction.getBook();
                    if (book.getAvailableCopies() > 0) {
                        book.setAvailableCopies(book.getAvailableCopies() - 1);
                        bookRepository.save(book);
                    }
                }
            } catch (IllegalStateException e) {
                // Re-throw business rule violations
                throw e;
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status: " + status);
            }
        }

        // Update book condition if provided
        if (bookCondition != null && !bookCondition.isEmpty()) {
            try {
                BookCondition condition = BookCondition.valueOf(bookCondition.toUpperCase());
                transaction.setBookCondition(condition);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid book condition: " + bookCondition);
            }
        }
        
        // Update condition notes if provided
        if (conditionNotes != null) {
            transaction.setConditionNotes(conditionNotes);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToDTO(savedTransaction);
    }

    /**
     * Convert Transaction entity to DTO
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .memberId(transaction.getUser().getUsername()) // Using username as member ID
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
