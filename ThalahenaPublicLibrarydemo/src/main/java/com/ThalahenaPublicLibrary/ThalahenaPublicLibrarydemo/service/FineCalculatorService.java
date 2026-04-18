package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Fine Calculator Service - Calculates fines for overdue book returns
 * 
 * SRP (Single Responsibility Principle):
 * - This service ONLY handles fine calculation logic
 * - Does NOT handle database operations (that's FineRepository's job)
 * - Does NOT handle HTTP requests (that's Controller's job)
 * - Single purpose: calculate fines based on dates
 * 
 * OCP (Open/Closed Principle):
 * - Open for extension: Can add new fine calculation strategies
 * - Closed for modification: FINE_RATE_PER_DAY can be changed without modifying calculation logic
 * - Future: Can add different rates for different book categories
 * - Future: Can add max fine cap without changing existing logic
 * 
 * DIP (Dependency Inversion Principle):
 * - No external dependencies - pure calculation service
 * - If needed in future, can depend on configuration abstraction for fine rate
 * - Spring manages this as a service bean
 */
@Service
public class FineCalculatorService {
    
    // Fine rate per day - easily configurable (OCP)
    private static final double FINE_RATE_PER_DAY = 5.0; // Rs. 5 per day
    
    /**
     * Calculate fine based on due date and return date
     * 
     * @param dueDate The date when the book should have been returned
     * @param returnDate The actual date when the book was returned
     * @return Fine amount (0.0 if returned on time or early)
     */
    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        // Validation
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date cannot be null");
        }
        if (returnDate == null) {
            throw new IllegalArgumentException("Return date cannot be null");
        }
        
        // If returned on time or early, no fine
        if (!returnDate.isAfter(dueDate)) {
            return 0.0;
        }
        
        // Calculate days overdue and apply fine rate
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, returnDate);
        return Math.max(0, daysOverdue * FINE_RATE_PER_DAY);
    }
    
    /**
     * Calculate fine assuming return is today
     * Useful for fine preview before actual return
     */
    public double calculateFineForToday(LocalDate dueDate) {
        return calculateFine(dueDate, LocalDate.now());
    }
    
    /**
     * Get number of days overdue
     * 
     * @return Number of days (0 if not overdue)
     */
    public long getDaysOverdue(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate == null || !returnDate.isAfter(dueDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, returnDate);
    }
    
    /**
     * Get fine rate per day (for display purposes)
     */
    public double getFineRatePerDay() {
        return FINE_RATE_PER_DAY;
    }
}
