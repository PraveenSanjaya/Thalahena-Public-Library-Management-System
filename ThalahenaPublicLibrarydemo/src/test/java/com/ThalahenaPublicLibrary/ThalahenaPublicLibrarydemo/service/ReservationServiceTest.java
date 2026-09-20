package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.ReservationDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.*;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.BookRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.ReservationRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pure Mockito unit tests for ReservationService — no Spring context, no database.
 *
 * NOTE on auto-expiry: Reservation.prePersist() computes expiryDate = reservationDate + 3 days,
 * which only runs via JPA lifecycle callbacks (not exercised by mocked repositories here), and
 * ReservationExpiryTask (a @Scheduled hourly job) cancels PENDING reservations past that expiry.
 * Those two behaviors need a real persistence context / clock control and are covered separately;
 * this class focuses on ReservationService's own branching logic.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private BookRepository bookRepository;
    @Mock private IssuanceService issuanceService;

    @InjectMocks
    private ReservationService reservationService;

    private User member;
    private Book book;

    @BeforeEach
    void setUp() {
        member = User.builder().id(1L).username("member1").role(Role.MEMBER).build();
        book = Book.builder().id(10L).title("Domain-Driven Design").availableCopies(0).totalCopies(2).build();
    }

    @Test
    void createReservation_valid_succeeds() {
        ReservationDTO request = ReservationDTO.builder().userId(1L).bookId(10L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> {
            Reservation r = inv.getArgument(0);
            r.setId(500L);
            return r;
        });

        ReservationDTO result = reservationService.createReservation(request);

        assertEquals("PENDING", result.getStatus());
        assertEquals(1L, result.getUserId());
        assertEquals(10L, result.getBookId());
        assertFalse(result.getProcessed());
    }

    @Test
    void createReservation_missingUserId_throws() {
        ReservationDTO request = ReservationDTO.builder().bookId(10L).build();
        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(request));
        verifyNoInteractions(reservationRepository);
    }

    @Test
    void createReservation_missingBookId_throws() {
        ReservationDTO request = ReservationDTO.builder().userId(1L).build();
        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void createReservation_invalidUser_throws() {
        ReservationDTO request = ReservationDTO.builder().userId(404L).bookId(10L).build();
        when(userRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void createReservation_invalidBook_throws() {
        ReservationDTO request = ReservationDTO.builder().userId(1L).bookId(999L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservationService.createReservation(request));
    }

    @Test
    void updateStatus_approve_mapsToAvailableAndAutoIssues() {
        Reservation reservation = Reservation.builder().id(1L).user(member).book(book)
                .status(ReservationStatus.PENDING).processed(false)
                .reservationDate(LocalDateTime.now()).build();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationDTO result = reservationService.updateStatus(1L, "APPROVED");

        assertEquals("AVAILABLE", result.getStatus());
        verify(issuanceService, times(1)).issueBookForReservation(reservation);
    }

    @Test
    void updateStatus_reject_marksProcessedAndDoesNotIssue() {
        Reservation reservation = Reservation.builder().id(2L).user(member).book(book)
                .status(ReservationStatus.PENDING).processed(false).build();
        when(reservationRepository.findById(2L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationDTO result = reservationService.updateStatus(2L, "REJECTED");

        assertEquals("UNAVAILABLE", result.getStatus());
        assertTrue(result.getProcessed());
        verify(issuanceService, never()).issueBookForReservation(any());
    }

    @Test
    void updateStatus_invalidStatusString_throws() {
        Reservation reservation = Reservation.builder().id(3L).user(member).book(book)
                .status(ReservationStatus.PENDING).build();
        when(reservationRepository.findById(3L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalArgumentException.class, () -> reservationService.updateStatus(3L, "NOT_A_STATUS"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void updateStatus_alreadyCompleted_cannotBeModified() {
        Reservation reservation = Reservation.builder().id(4L).user(member).book(book)
                .status(ReservationStatus.COMPLETED).build();
        when(reservationRepository.findById(4L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.updateStatus(4L, "APPROVED"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void updateStatus_alreadyCancelled_cannotBeModified() {
        Reservation reservation = Reservation.builder().id(5L).user(member).book(book)
                .status(ReservationStatus.CANCELLED).build();
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.updateStatus(5L, "AVAILABLE"));
    }

    @Test
    void updateStatus_reservationNotFound_throws() {
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservationService.updateStatus(999L, "APPROVED"));
    }

    @Test
    void acknowledgeReservation_alreadyProcessed_throws() {
        Reservation reservation = Reservation.builder().id(6L).user(member).book(book)
                .status(ReservationStatus.AVAILABLE).processed(true).build();
        when(reservationRepository.findById(6L)).thenReturn(Optional.of(reservation));

        assertThrows(IllegalStateException.class, () -> reservationService.acknowledgeReservation(6L));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void acknowledgeReservation_available_autoIssuesAndMarksProcessed() {
        Reservation reservation = Reservation.builder().id(7L).user(member).book(book)
                .status(ReservationStatus.AVAILABLE).processed(false).build();
        when(reservationRepository.findById(7L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservationDTO result = reservationService.acknowledgeReservation(7L);

        assertTrue(result.getProcessed());
        verify(issuanceService, times(1)).issueBookForReservation(reservation);
    }
}
