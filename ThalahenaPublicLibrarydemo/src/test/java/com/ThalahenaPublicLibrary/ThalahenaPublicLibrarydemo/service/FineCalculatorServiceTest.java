package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for FineCalculatorService
 * 
 * Tests cover:
 * 1. Late return - fine calculated correctly
 * 2. On-time return - fine = 0
 * 3. Early return - fine = 0
 * 4. Same day return - fine = 0
 * 5. Edge cases
 */
@SpringBootTest
class FineCalculatorServiceTest {

    @Autowired
    private FineCalculatorService fineCalculatorService;

    @Test
    void testLateReturn_FineCalculatedCorrectly() {
        // Scenario: Book returned 4 days late
        // Given: due date = 2026-04-08
        // When: return date = 2026-04-12
        // Then: fine = 4 * 5 = 20
        
        LocalDate dueDate = LocalDate.of(2026, 4, 8);
        LocalDate returnDate = LocalDate.of(2026, 4, 12);
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        long daysOverdue = fineCalculatorService.getDaysOverdue(dueDate, returnDate);
        
        assertEquals(20.0, fine, 0.01); // Rs. 20
        assertEquals(4, daysOverdue); // 4 days late
    }

    @Test
    void testLateReturn_OneDayLate() {
        // Scenario: Book returned 1 day late
        LocalDate dueDate = LocalDate.of(2026, 4, 8);
        LocalDate returnDate = LocalDate.of(2026, 4, 9);
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        
        assertEquals(5.0, fine, 0.01); // Rs. 5
    }

    @Test
    void testOnTimeReturn_FineIsZero() {
        // Scenario: Book returned exactly on due date
        // Given: due date = 2026-04-08
        // When: return date = 2026-04-08
        // Then: fine = 0
        
        LocalDate dueDate = LocalDate.of(2026, 4, 8);
        LocalDate returnDate = LocalDate.of(2026, 4, 8);
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        long daysOverdue = fineCalculatorService.getDaysOverdue(dueDate, returnDate);
        
        assertEquals(0.0, fine, 0.01);
        assertEquals(0, daysOverdue);
    }

    @Test
    void testEarlyReturn_FineIsZero() {
        // Scenario: Book returned 3 days early
        LocalDate dueDate = LocalDate.of(2026, 4, 8);
        LocalDate returnDate = LocalDate.of(2026, 4, 5);
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        long daysOverdue = fineCalculatorService.getDaysOverdue(dueDate, returnDate);
        
        assertEquals(0.0, fine, 0.01);
        assertEquals(0, daysOverdue);
    }

    @Test
    void testVeryLateReturn_LargeFine() {
        // Scenario: Book returned 30 days late
        LocalDate dueDate = LocalDate.of(2026, 4, 1);
        LocalDate returnDate = LocalDate.of(2026, 5, 1);
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        long daysOverdue = fineCalculatorService.getDaysOverdue(dueDate, returnDate);
        
        assertEquals(150.0, fine, 0.01); // 30 * 5 = 150
        assertEquals(30, daysOverdue);
    }

    @Test
    void testCalculateFineForToday() {
        // Scenario: Calculate fine assuming return is today
        LocalDate dueDate = LocalDate.now().minusDays(3); // 3 days ago
        
        double fine = fineCalculatorService.calculateFineForToday(dueDate);
        
        assertEquals(15.0, fine, 0.01); // 3 * 5 = 15
    }

    @Test
    void testGetFineRatePerDay() {
        assertEquals(5.0, fineCalculatorService.getFineRatePerDay(), 0.01);
    }

    @Test
    void testDueDateIsNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            fineCalculatorService.calculateFine(null, LocalDate.now());
        });
    }

    @Test
    void testReturnDateIsNull_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            fineCalculatorService.calculateFine(LocalDate.now(), null);
        });
    }

    @Test
    void testEdgeCase_WeekendReturn() {
        // Scenario: Due date is Friday, returned on Monday
        LocalDate dueDate = LocalDate.of(2026, 4, 10); // Friday
        LocalDate returnDate = LocalDate.of(2026, 4, 13); // Monday
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        long daysOverdue = fineCalculatorService.getDaysOverdue(dueDate, returnDate);
        
        // System counts calendar days, not business days
        assertEquals(15.0, fine, 0.01); // 3 * 5 = 15
        assertEquals(3, daysOverdue);
    }

    @Test
    void testEdgeCase_MonthBoundary() {
        // Scenario: Due date end of month, returned next month
        LocalDate dueDate = LocalDate.of(2026, 4, 30);
        LocalDate returnDate = LocalDate.of(2026, 5, 2);
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        
        assertEquals(10.0, fine, 0.01); // 2 * 5 = 10
    }

    @Test
    void testEdgeCase_YearBoundary() {
        // Scenario: Due date end of year, returned next year
        LocalDate dueDate = LocalDate.of(2025, 12, 30);
        LocalDate returnDate = LocalDate.of(2026, 1, 2);
        
        double fine = fineCalculatorService.calculateFine(dueDate, returnDate);
        long daysOverdue = fineCalculatorService.getDaysOverdue(dueDate, returnDate);
        
        assertEquals(15.0, fine, 0.01); // 3 * 5 = 15
        assertEquals(3, daysOverdue);
    }
}
