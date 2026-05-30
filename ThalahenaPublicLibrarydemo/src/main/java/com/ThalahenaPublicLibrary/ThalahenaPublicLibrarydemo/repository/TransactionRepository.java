package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Transaction;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.TransactionStatus;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    
    /**
     * Find all active (ISSUED or OVERDUE) transactions for a specific user
     * Used to enforce: One member can borrow only ONE book at a time
     */
    @Query("SELECT t FROM Transaction t WHERE t.user = :user AND t.status IN ('ISSUED', 'OVERDUE')")
    List<Transaction> findActiveTransactionsByUser(@Param("user") User user);
    
    // Find transactions by userId ordered by issue date
    List<Transaction> findByUserIdOrderByIssueDateDesc(Long userId);
    
    // Count by status
    Long countByStatus(TransactionStatus status);
    
    // Find overdue transactions
    @Query("SELECT t FROM Transaction t WHERE t.status = 'ISSUED' AND t.dueDate < :today")
    List<Transaction> findOverdueTransactions(@Param("today") LocalDate today);
    
    // Find transactions by status
    List<Transaction> findByStatus(TransactionStatus status);
    
    // Find transactions with filtering
    @Query("SELECT t FROM Transaction t WHERE " +
           "(:userId IS NULL OR t.user.id = :userId) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:dateFrom IS NULL OR t.issueDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR t.issueDate <= :dateTo)")
    List<Transaction> findByFilters(
        @Param("userId") Long userId,
        @Param("status") TransactionStatus status,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo
    );
    
    // Count transactions by book
    @Query("SELECT t.book.id, COUNT(t) FROM Transaction t GROUP BY t.book.id")
    List<Object[]> countTransactionsByBook();
}
