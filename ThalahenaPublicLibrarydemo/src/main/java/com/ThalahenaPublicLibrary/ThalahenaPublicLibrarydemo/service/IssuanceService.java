package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.*;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.BookRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * IssuanceService - Handles automatic book issuance triggered by reservation approval
 * 
 * SRP (Single Responsibility Principle):
 * - Only responsible for creating borrow transactions from approved reservations
 * - Does NOT handle reservation CRUD (that's ReservationService's job)
 * 
 * OCP (Open/Closed Principle):
 * - Can extend to support different loan periods, member types, etc.
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on repository abstractions injected by Spring
 */
@Service
public class IssuanceService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Auto-issue a book for an approved reservation.
     * 
     * Workflow:
     * 1. Verify book has available copies
     * 2. Verify member has no other active borrow (one-book-per-member rule)
     * 3. Decrement available copies
     * 4. Create ISSUED transaction
     * 5. Mark reservation as COMPLETED
     * 
     * @param reservation the approved reservation
     * @throws IllegalStateException if copies are unavailable or member already has an active borrow
     */
    @Transactional
    public void issueBookForReservation(Reservation reservation) {
        Book book = reservation.getBook();
        User user = reservation.getUser();

        // Check available copies
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException(
                    "No copies available for auto-issue. Book '" + book.getTitle() + "' is out of stock."
            );
        }

        // Enforce one-book-per-member rule (same validation as TransactionService.issueBook)
        List<Transaction> activeTransactions = transactionRepository.findActiveTransactionsByUser(user);
        if (!activeTransactions.isEmpty()) {
            Transaction active = activeTransactions.get(0);
            throw new IllegalStateException(
                    "Member '" + user.getUsername() + "' already has an active borrow ('" +
                    active.getBook().getTitle() + "', Status: " + active.getStatus() + "). " +
                    "Cannot auto-issue reservation."
            );
        }

        // Reduce available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Create borrow transaction (2-week loan period)
        Transaction transaction = Transaction.builder()
                .user(user)
                .book(book)
                .issueDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(14))
                .status(TransactionStatus.ISSUED)
                .fineAmount(0.0)
                .bookCondition(BookCondition.GOOD)
                .build();

        transactionRepository.save(transaction);

        // Mark reservation as fulfilled
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation.setProcessed(true);
    }
}
