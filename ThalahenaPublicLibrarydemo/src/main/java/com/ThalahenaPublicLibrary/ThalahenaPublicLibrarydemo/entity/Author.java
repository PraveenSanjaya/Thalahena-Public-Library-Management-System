package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

/**
 * SOLID Principles Applied:
 * SRP: Only represents Author data.
 * OCP: Extensible with new fields without breaking existing code.
 * DIP: Depends on JPA abstraction (Jakarta Persistence).
 */
@Entity
@Table(name = "authors", indexes = {
    @Index(name = "idx_author_name", columnList = "name")
})
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("author")
    private List<Book> books = new ArrayList<>();

    // ── Constructors ─────────────────────────────────────────────────────────
    public Author() {}

    public Author(Long id, String name, String bio, List<Book> books) {
        this.id = id; this.name = name; this.bio = bio;
        this.books = books != null ? books : new ArrayList<>();
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static AuthorBuilder builder() { return new AuthorBuilder(); }

    public static class AuthorBuilder {
        private Long id;
        private String name;
        private String bio;
        private List<Book> books = new ArrayList<>();

        public AuthorBuilder id(Long id)             { this.id = id; return this; }
        public AuthorBuilder name(String name)       { this.name = name; return this; }
        public AuthorBuilder bio(String bio)         { this.bio = bio; return this; }
        public AuthorBuilder books(List<Book> books) { this.books = books; return this; }

        public Author build() {
            Author a = new Author();
            a.id = this.id; a.name = this.name; a.bio = this.bio;
            a.books = this.books != null ? this.books : new ArrayList<>();
            return a;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                 { return id; }
    public void setId(Long id)         { this.id = id; }
    public String getName()             { return name; }
    public void setName(String name)   { this.name = name; }
    public String getBio()              { return bio; }
    public void setBio(String bio)     { this.bio = bio; }
    public List<Book> getBooks()        { return books; }
    public void setBooks(List<Book> b) { this.books = b; }
}
