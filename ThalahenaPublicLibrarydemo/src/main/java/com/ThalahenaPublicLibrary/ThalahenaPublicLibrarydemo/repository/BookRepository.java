package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Search books
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Book> searchBooks(@Param("search") String search);
    
    // Find books by category
    List<Book> findByCategory(String category);
    
    // Get category distribution
    @Query("SELECT b.category, COUNT(b) FROM Book b GROUP BY b.category")
    List<Object[]> getCategoryDistribution();
    
    // Get top borrowed books
    @Query("SELECT b.id, b.title, COUNT(t) as borrowCount " +
           "FROM Book b LEFT JOIN Transaction t ON b.id = t.book.id " +
           "GROUP BY b.id, b.title ORDER BY borrowCount DESC")
    List<Object[]> getTopBorrowedBooks();
}
