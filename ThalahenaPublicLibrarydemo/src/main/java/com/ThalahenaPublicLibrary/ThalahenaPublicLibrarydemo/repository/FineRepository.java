package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Fine;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.FineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {
    // Find fines by status
    List<Fine> findByStatus(FineStatus status);
    
    // Sum unpaid fines
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.status = 'UNPAID'")
    Double sumUnpaidFines();
    
    // Sum paid fines
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.status = 'PAID'")
    Double sumPaidFines();
    
    // Get all fines with transaction and user details
    @Query("SELECT f FROM Fine f JOIN f.transaction t JOIN t.user u")
    List<Fine> findAllWithDetails();
}
