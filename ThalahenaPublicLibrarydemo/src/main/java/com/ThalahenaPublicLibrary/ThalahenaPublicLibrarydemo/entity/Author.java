package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SOLID Principles Applied:
 * 
 * SRP (Single Responsibility Principle):
 * - This entity is only responsible for representing an Author
 * - Book relationships are managed by the Book entity, not here
 * 
 * OCP (Open/Closed Principle):
 * - Entity can be extended with new fields without modifying existing code
 * - Example: Adding biography, nationality fields doesn't break existing functionality
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on JPA abstraction (Jakarta Persistence), not concrete database implementation
 */
@Entity
@Table(name = "authors", indexes = {
    @Index(name = "idx_author_name", columnList = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;
    
    // One author can write many books
    // mappedBy indicates that Book entity owns the relationship
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Book> books = new ArrayList<>();
}
