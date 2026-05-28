package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_isbn", columnList = "isbn"),
    @Index(name = "idx_category", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @JsonIgnoreProperties("books")
    private Author author;

    @Column(unique = true)
    private String isbn;
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String coverImage;
    private String publisher;
    private java.time.LocalDate dateReceived;
    
    @Builder.Default
    private int availableCopies = 0;
    
    @Builder.Default
    private int totalCopies = 0;
}
