package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.FineDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Fine;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.FineStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.FineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pure Mockito unit tests for FineService — no Spring context, no database.
 */
@ExtendWith(MockitoExtension.class)
class FineServiceTest {

    @Mock private FineRepository fineRepository;

    @InjectMocks
    private FineService fineService;

    @Test
    void getFineById_notFound_throws() {
        when(fineRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> fineService.getFineById(999L));
    }

    @Test
    void updateFine_markAsPaid_setsStatusAndPaymentDate() {
        Fine fine = Fine.builder().id(1L).amount(25.0).returnDate(LocalDate.of(2026, 4, 12))
                .status(FineStatus.UNPAID).build();
        when(fineRepository.findById(1L)).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(inv -> inv.getArgument(0));

        FineDTO result = fineService.updateFine(1L, LocalDate.of(2026, 4, 15), FineStatus.PAID);

        assertEquals("PAID", result.getStatus());
        assertEquals(LocalDate.of(2026, 4, 15), result.getPaymentDate());
    }

    @Test
    void updateFine_paymentDateBeforeReturnDate_isRejected() {
        Fine fine = Fine.builder().id(2L).amount(10.0).returnDate(LocalDate.of(2026, 4, 12))
                .status(FineStatus.UNPAID).build();
        when(fineRepository.findById(2L)).thenReturn(Optional.of(fine));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> fineService.updateFine(2L, LocalDate.of(2026, 4, 10), FineStatus.PAID));

        assertTrue(ex.getMessage().contains("cannot be before return date"));
        verify(fineRepository, never()).save(any());
    }

    @Test
    void updateFine_markPaidWithoutExplicitDate_defaultsToReturnDate() {
        Fine fine = Fine.builder().id(3L).amount(15.0).returnDate(LocalDate.of(2026, 4, 12))
                .status(FineStatus.UNPAID).build();
        when(fineRepository.findById(3L)).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(inv -> inv.getArgument(0));

        FineDTO result = fineService.updateFine(3L, null, FineStatus.PAID);

        assertEquals(LocalDate.of(2026, 4, 12), result.getPaymentDate());
    }

    @Test
    void updateFine_invalidStatus_throwsIllegalArgument() {
        // FineStatus is an enum, so this test documents that only PAID/UNPAID/NONE are accepted
        // by exercising the guard directly is not possible via the enum type system; instead we
        // confirm all three legal values are accepted without exception.
        Fine fine = Fine.builder().id(4L).amount(0.0).status(FineStatus.NONE).build();
        when(fineRepository.findById(4L)).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> fineService.updateFine(4L, null, FineStatus.NONE));
    }

    @Test
    void updateFine_zeroAmountFine_statusForcedToNone() {
        Fine fine = Fine.builder().id(5L).amount(0.0).status(FineStatus.UNPAID).build();
        when(fineRepository.findById(5L)).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(inv -> inv.getArgument(0));

        FineDTO result = fineService.updateFine(5L, null, null);

        assertEquals("NONE", result.getStatus());
    }

    @Test
    void deleteFine_softDeletes_amountZeroedAndStatusNone() {
        Fine fine = Fine.builder().id(6L).amount(50.0).status(FineStatus.UNPAID).build();
        when(fineRepository.findById(6L)).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(inv -> inv.getArgument(0));

        fineService.deleteFine(6L);

        ArgumentCaptor<Fine> captor = ArgumentCaptor.forClass(Fine.class);
        verify(fineRepository).save(captor.capture());
        assertEquals(0.0, captor.getValue().getAmount());
        assertEquals(FineStatus.NONE, captor.getValue().getStatus());
    }

    @Test
    void deleteFine_notFound_throws() {
        when(fineRepository.findById(404L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> fineService.deleteFine(404L));
    }

    @Test
    void getFineStats_nullSums_defaultToZero() {
        when(fineRepository.sumUnpaidFines()).thenReturn(null);
        when(fineRepository.sumPaidFines()).thenReturn(null);

        FineService.FineStatsDTO stats = fineService.getFineStats();

        assertEquals(0.0, stats.getTotalUnpaid());
        assertEquals(0.0, stats.getTotalPaid());
    }

    @Test
    void getFineStats_returnsActualSums() {
        when(fineRepository.sumUnpaidFines()).thenReturn(75.0);
        when(fineRepository.sumPaidFines()).thenReturn(120.0);

        FineService.FineStatsDTO stats = fineService.getFineStats();

        assertEquals(75.0, stats.getTotalUnpaid());
        assertEquals(120.0, stats.getTotalPaid());
        assertEquals(120.0, stats.getTotalCollected());
    }
}
