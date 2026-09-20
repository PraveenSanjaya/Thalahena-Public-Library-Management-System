package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.*;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.BookRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pure Mockito unit tests for IssuanceService — the auto-issue path triggered when
 * a reservation is approved. Duplicates (intentionally, per the source) the one-book-per-member
 * enforcement found in TransactionService.issueBook.
 */
@ExtendWith(MockitoExtension.class)
class IssuanceServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private BookRepository bookRepository;

    @InjectMocks
    private IssuanceService issuanceService;

    @Test
    void issueBookForReservation_noActiveBorrow_succeeds() {
        User member = User.builder().id(1L).username("member1").role(Role.MEMBER).build();
        Book book = Book.builder().id(10L).title("Effective Java").availableCopies(1).build();
        Reservation reservation = Reservation.builder().id(1L).user(member).book(book)
                .status(ReservationStatus.AVAILABLE).build();

        when(transactionRepository.findActiveTransactionsByUser(member)).thenReturn(Collections.emptyList());

        issuanceService.issueBookForReservation(reservation);

        assertEquals(0, book.getAvailableCopies());
        assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
        assertTrue(reservation.getProcessed());
        verify(bookRepository).save(book);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void issueBookForReservation_noAvailableCopies_isRejected() {
        User member = User.builder().id(1L).username("member1").role(Role.MEMBER).build();
        Book book = Book.builder().id(11L).title("Out Of Stock Book").availableCopies(0).build();
        Reservation reservation = Reservation.builder().id(2L).user(member).book(book)
                .status(ReservationStatus.AVAILABLE).build();

        assertThrows(IllegalStateException.class, () -> issuanceService.issueBookForReservation(reservation));

        verify(bookRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
        assertNotEquals(ReservationStatus.COMPLETED, reservation.getStatus());
    }

    @Test
    void issueBookForReservation_memberAlreadyHasActiveBorrow_isRejected() {
        User member = User.builder().id(1L).username("member1").role(Role.MEMBER).build();
        Book requestedBook = Book.builder().id(12L).title("Requested Book").availableCopies(1).build();
        Book borrowedBook = Book.builder().id(13L).title("Currently Borrowed").availableCopies(0).build();
        Transaction activeTransaction = Transaction.builder().id(900L).user(member).book(borrowedBook)
                .status(TransactionStatus.ISSUED).build();
        Reservation reservation = Reservation.builder().id(3L).user(member).book(requestedBook)
                .status(ReservationStatus.AVAILABLE).build();

        when(transactionRepository.findActiveTransactionsByUser(member)).thenReturn(List.of(activeTransaction));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> issuanceService.issueBookForReservation(reservation));

        assertTrue(ex.getMessage().contains("Currently Borrowed"));
        assertEquals(1, requestedBook.getAvailableCopies(), "Requested book's inventory must be untouched");
        verify(bookRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}
