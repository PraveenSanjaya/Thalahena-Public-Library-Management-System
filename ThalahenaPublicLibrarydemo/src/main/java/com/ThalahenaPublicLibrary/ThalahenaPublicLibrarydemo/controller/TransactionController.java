package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.TransactionDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.*;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Transaction Controller - Handles HTTP requests for book borrowing and returning
 * 
 * SRP: Only handles HTTP request/response, delegates to TransactionService
 * OCP: Can add new endpoints without modifying existing ones
 * DIP: Depends on TransactionService abstraction, not concrete implementation
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping({"/api/staff/transactions", "/api/transactions"})
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;

    /**
     * GET /api/staff/transactions?status=All|Issue|Overdue|Return
     * Get transactions with optional status filter
     */
    @GetMapping
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(
            @RequestParam(required = false) String status) {
        List<TransactionDTO> transactions = transactionService.getTransactions(status);
        return ResponseEntity.ok(transactions);
    }

    /**
     * GET /api/staff/transactions/counters
     * Get transaction counters for dashboard cards
     */
    @GetMapping("/counters")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getCounters() {
        TransactionService.TransactionCountersDTO counters = transactionService.getCounters();
        return ResponseEntity.ok(counters);
    }

    /**
     * POST /api/staff/transactions/issue?userId=X&bookId=Y
     * Issue a book to a member
     */
    @PostMapping("/issue")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> issueBook(
            @RequestParam Long userId,
            @RequestParam Long bookId) {
        try {
            TransactionDTO transaction = transactionService.issueBook(userId, bookId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error issuing book: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/transactions/return/{issueId}
     * Return a book
     * SRP: Only handles HTTP request/response, delegates to TransactionService
     */
    @PutMapping("/return/{issueId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> returnBook(
            @PathVariable Long issueId,
            @RequestParam(required = false) LocalDate returnDate,
            @RequestParam(required = false) String bookCondition,
            @RequestParam(required = false) String conditionNotes) {
        try {
            // Validation: Return Date cannot be empty
            if (returnDate == null) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Return Date is required"));
            }
            
            // Validation: Book Condition is mandatory
            if (bookCondition == null || bookCondition.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Book Condition is required"));
            }
            
            BookCondition condition = BookCondition.valueOf(bookCondition);
            
            TransactionDTO transaction = transactionService.returnBook(
                    issueId, returnDate, condition, conditionNotes);
            
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid book condition: " + bookCondition));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error returning book: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/transactions/{id}/update
     * Update transaction status and/or book condition
     */
    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateTransaction(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String bookCondition,
            @RequestParam(required = false) String conditionNotes) {
        try {
            TransactionDTO transaction = transactionService.updateTransaction(
                    id, status, bookCondition, conditionNotes);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating transaction: " + e.getMessage()));
        }
    }

    /**
     * GET /api/transactions/user/{userId}
     * Get transaction history for a specific user
     * STAFF/ADMIN can view any user's history.
     * MEMBER can only view their own history (SpEL: #userId == principal.id).
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN') or (hasRole('MEMBER') and #userId == principal.id)")
    public ResponseEntity<?> getUserTransactionHistory(@PathVariable Long userId) {
        try {
            List<TransactionDTO> history = transactionService.getUserTransactionHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching transaction history: " + e.getMessage()));
        }
    }
}
