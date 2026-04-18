package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * SRP: This repository only handles Author database operations
 * DIP: Depends on Spring Data JPA abstraction, not concrete SQL implementation
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
    
    // Find author by name (case-insensitive)
    Optional<Author> findByNameIgnoreCase(String name);
    
    // Search authors by name
    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Author> searchByName(@Param("search") String search);
    
    // Check if author exists by name
    boolean existsByNameIgnoreCase(String name);
}
