package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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

    @Min(value = 1, message = "Pages must be at least 1")
    @Column(nullable = false, columnDefinition = "INT NOT NULL DEFAULT 1")
    @Builder.Default
    private Integer pages = 1;

    @Size(max = 20, message = "Dewey code must be at most 20 characters")
    @Column(name = "dewey_code", nullable = false, length = 20, columnDefinition = "VARCHAR(20) NOT NULL DEFAULT '000'")
    @Builder.Default
    private String deweyCode = "000";

    @Size(max = 20, message = "Municipal reference must be at most 20 characters")
    @Column(name = "municipal_ref", length = 20)
    private String municipalRef;

    @Size(max = 20, message = "Library reference must be at most 20 characters")
    @Column(name = "library_ref", length = 20)
    private String libraryRef;
}
