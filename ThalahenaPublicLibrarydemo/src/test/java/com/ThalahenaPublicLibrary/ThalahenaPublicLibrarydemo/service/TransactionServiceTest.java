package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.TransactionDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.*;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pure Mockito unit tests for TransactionService — no Spring context, no database.
 *
 * Covers the circulation business rules:
 *  - One member may have only ONE actively issued (ISSUED/OVERDUE) book at a time
 *  - Issuing decrements availableCopies, returning increments it
 *  - Returning creates a Fine record (even when the amount is zero)
 *  - Rejected issue attempts must not create a transaction or mutate book state
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private BookRepository bookRepository;
    @Mock private UserRepository userRepository;
    @Mock private FineRepository fineRepository;
    @Mock private FineCalculatorService fineCalculatorService;

    @InjectMocks
    private TransactionService transactionService;

    private User member;
    private Book book;

    @BeforeEach
    void setUp() {
        member = User.builder().id(1L).username("member1").email("member1@gmail.com").role(Role.MEMBER).build();
        book = Book.builder().id(10L).title("Clean Code").isbn("978-0132350884").totalCopies(3).availableCopies(2).build();
    }

    // ---------- ISSUE: success path ----------

    @Test
    void issueBook_memberHasNoActiveIssue_succeeds() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findActiveTransactionsByUser(member)).thenReturn(Collections.emptyList());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> {
            Transaction t = inv.getArgument(0);
            t.setId(100L);
            return t;
        });

        TransactionDTO result = transactionService.issueBook(1L, 10L);

        assertNotNull(result);
        assertEquals("ISSUED", result.getStatus());
        assertEquals(1, book.getAvailableCopies()); // 2 -> 1
        assertEquals(LocalDate.now().plusDays(14), result.getDueDate());
        verify(bookRepository, times(1)).save(book);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void issueBook_bookNotFound_throwsAndDoesNotTouchState() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.issueBook(1L, 99L));

        verify(transactionRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void issueBook_memberNotFound_throws() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.issueBook(404L, 10L));

        verifyNoInteractions(bookRepository);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void issueBook_noAvailableCopies_isRejected() {
        book.setAvailableCopies(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> transactionService.issueBook(1L, 10L));

        assertTrue(ex.getMessage().toLowerCase().contains("unavailable"));
        assertEquals(0, book.getAvailableCopies());
        verify(bookRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    // ---------- ONE-BOOK-PER-MEMBER regression test ----------

    @Test
    void issueBook_memberAlreadyHasActiveIssue_isRejected_noStateChange() {
        Book alreadyBorrowed = Book.builder().id(20L).title("The Pragmatic Programmer").availableCopies(1).totalCopies(1).build();
        Transaction activeTransaction = Transaction.builder()
                .id(555L).user(member).book(alreadyBorrowed)
                .issueDate(LocalDate.now().minusDays(2))
                .dueDate(LocalDate.now().plusDays(12))
                .status(TransactionStatus.ISSUED)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findActiveTransactionsByUser(member)).thenReturn(List.of(activeTransaction));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> transactionService.issueBook(1L, 10L));

        assertTrue(ex.getMessage().contains("The Pragmatic Programmer"));
        assertTrue(ex.getMessage().toLowerCase().contains("one book"));

        // Requirement: no new transaction created, no available-copy mutation, no partial state change
        verify(transactionRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
        assertEquals(2, book.getAvailableCopies(), "Book B's inventory must be untouched by the rejected issue");
    }

    @Test
    void issueBook_memberHasOverdueBook_isAlsoRejected() {
        Transaction overdueTransaction = Transaction.builder()
                .id(556L).user(member).book(book)
                .status(TransactionStatus.OVERDUE)
                .dueDate(LocalDate.now().minusDays(5))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(transactionRepository.findActiveTransactionsByUser(member)).thenReturn(List.of(overdueTransaction));

        assertThrows(IllegalStateException.class, () -> transactionService.issueBook(1L, 10L));
        verify(transactionRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void issueBook_memberWithPreviouslyReturnedBook_canBorrowAgain() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        // RETURNED is not an active status, so the repository query (real impl) would not return it —
        // simulate that correctly here.
        when(transactionRepository.findActiveTransactionsByUser(member)).thenReturn(Collections.emptyList());
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionDTO result = transactionService.issueBook(1L, 10L);

        assertEquals("ISSUED", result.getStatus());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // ---------- RETURN ----------

    @Test
    void returnBook_onTime_noFineCreatedWithZeroAmount() {
        Transaction t = Transaction.builder()
                .id(1L).user(member).book(book)
                .issueDate(LocalDate.now().minusDays(10))
                .dueDate(LocalDate.now())
                .status(TransactionStatus.ISSUED)
                .fineAmount(0.0)
                .build();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(t));
        when(fineCalculatorService.calculateFine(t.getDueDate(), LocalDate.now())).thenReturn(0.0);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionDTO result = transactionService.returnBook(1L, LocalDate.now(), BookCondition.GOOD, null);

        assertEquals("RETURNED", result.getStatus());
        assertEquals(0.0, result.getFineAmount());
        assertEquals(3, book.getAvailableCopies()); // 2 -> 3

        ArgumentCaptor<Fine> fineCaptor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(fineCaptor.capture());
        assertEquals(FineStatus.NONE, fineCaptor.getValue().getStatus());
    }

    @Test
    void returnBook_overdue_createsUnpaidFine() {
        Transaction t = Transaction.builder()
                .id(2L).user(member).book(book)
                .issueDate(LocalDate.now().minusDays(19))
                .dueDate(LocalDate.now().minusDays(5))
                .status(TransactionStatus.OVERDUE)
                .fineAmount(0.0)
                .build();

        when(transactionRepository.findById(2L)).thenReturn(Optional.of(t));
        when(fineCalculatorService.calculateFine(t.getDueDate(), LocalDate.now())).thenReturn(25.0);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));

        TransactionDTO result = transactionService.returnBook(2L, LocalDate.now(), BookCondition.FAIR, "Slightly worn cover");

        assertEquals(25.0, result.getFineAmount());
        assertEquals("Slightly worn cover", result.getConditionNotes());

        ArgumentCaptor<Fine> fineCaptor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(fineCaptor.capture());
        assertEquals(FineStatus.UNPAID, fineCaptor.getValue().getStatus());
        assertEquals(25.0, fineCaptor.getValue().getAmount());
    }

    @Test
    void returnBook_alreadyReturned_isRejected() {
        Transaction t = Transaction.builder()
                .id(3L).user(member).book(book)
                .status(TransactionStatus.RETURNED)
                .returnDate(LocalDate.now().minusDays(1))
                .build();

        when(transactionRepository.findById(3L)).thenReturn(Optional.of(t));

        assertThrows(IllegalStateException.class,
                () -> transactionService.returnBook(3L, LocalDate.now(), BookCondition.GOOD, null));

        verify(fineRepository, never()).save(any());
        verify(bookRepository, never()).save(any());
    }

    @Test
    void returnBook_transactionNotFound_throws() {
        when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> transactionService.returnBook(999L, LocalDate.now(), BookCondition.GOOD, null));
    }

    @Test
    void returnBook_missingBookCondition_throwsIllegalArgument() {
        Transaction t = Transaction.builder()
                .id(4L).user(member).book(book)
                .dueDate(LocalDate.now())
                .status(TransactionStatus.ISSUED)
                .build();

        when(transactionRepository.findById(4L)).thenReturn(Optional.of(t));

        assertThrows(IllegalArgumentException.class,
                () -> transactionService.returnBook(4L, LocalDate.now(), null, null));

        verify(bookRepository, never()).save(any());
        verify(fineRepository, never()).save(any());
    }

    // ---------- updateTransaction: re-issuing must still enforce the rule ----------

    @Test
    void updateTransaction_cannotForceStatusToIssuedWhileAnotherActiveTransactionExists() {
        Transaction target = Transaction.builder().id(5L).user(member).book(book).status(TransactionStatus.RETURNED).build();
        Book otherBook = Book.builder().id(30L).title("Refactoring").availableCopies(1).build();
        Transaction otherActive = Transaction.builder().id(6L).user(member).book(otherBook).status(TransactionStatus.ISSUED).build();

        when(transactionRepository.findById(5L)).thenReturn(Optional.of(target));
        when(transactionRepository.findActiveTransactionsByUser(member)).thenReturn(List.of(otherActive));

        assertThrows(IllegalStateException.class,
                () -> transactionService.updateTransaction(5L, "ISSUED", null, null));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateTransaction_invalidStatus_throwsRuntimeException() {
        Transaction target = Transaction.builder().id(7L).user(member).book(book).status(TransactionStatus.ISSUED).build();
        when(transactionRepository.findById(7L)).thenReturn(Optional.of(target));

        assertThrows(RuntimeException.class,
                () -> transactionService.updateTransaction(7L, "NOT_A_REAL_STATUS", null, null));
    }
}
