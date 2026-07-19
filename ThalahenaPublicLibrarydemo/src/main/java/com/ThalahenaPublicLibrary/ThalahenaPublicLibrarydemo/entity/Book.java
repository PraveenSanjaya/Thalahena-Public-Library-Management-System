package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@Entity
@Table(name = "books", indexes = {
    @Index(name = "idx_title", columnList = "title"),
    @Index(name = "idx_isbn", columnList = "isbn"),
    @Index(name = "idx_category", columnList = "category")
})
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
    private LocalDate dateReceived;

    private int availableCopies = 0;
    private int totalCopies = 0;

    @Min(value = 1, message = "Pages must be at least 1")
    @Column(nullable = false)
    private Integer pages = 1;

    @NotBlank(message = "Dewey code is required")
    @Size(max = 20, message = "Dewey code must be at most 20 characters")
    @Column(name = "dewey_code", nullable = false, length = 20)
    private String deweyCode;

    @Size(max = 20, message = "Municipal reference must be at most 20 characters")
    @Column(name = "municipal_ref", length = 20)
    private String municipalRef;

    @Size(max = 20, message = "Library reference must be at most 20 characters")
    @Column(name = "library_ref", length = 20)
    private String libraryRef;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Book() {}

    public Book(Long id, String title, Author author, String isbn, String category,
                String description, String coverImage, String publisher, LocalDate dateReceived,
                int availableCopies, int totalCopies, Integer pages,
                String deweyCode, String municipalRef, String libraryRef) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.description = description;
        this.coverImage = coverImage;
        this.publisher = publisher;
        this.dateReceived = dateReceived;
        this.availableCopies = availableCopies;
        this.totalCopies = totalCopies;
        this.pages = pages;
        this.deweyCode = deweyCode;
        this.municipalRef = municipalRef;
        this.libraryRef = libraryRef;
    }

    // ── Builder ──────────────────────────────────────────────────────────────
    public static BookBuilder builder() { return new BookBuilder(); }

    public static class BookBuilder {
        private Long id;
        private String title;
        private Author author;
        private String isbn;
        private String category;
        private String description;
        private String coverImage;
        private String publisher;
        private LocalDate dateReceived;
        private int availableCopies = 0;
        private int totalCopies = 0;
        private Integer pages = 1;
        private String deweyCode;
        private String municipalRef;
        private String libraryRef;

        public BookBuilder id(Long id)                     { this.id = id; return this; }
        public BookBuilder title(String title)             { this.title = title; return this; }
        public BookBuilder author(Author author)           { this.author = author; return this; }
        public BookBuilder isbn(String isbn)               { this.isbn = isbn; return this; }
        public BookBuilder category(String category)       { this.category = category; return this; }
        public BookBuilder description(String description) { this.description = description; return this; }
        public BookBuilder coverImage(String coverImage)   { this.coverImage = coverImage; return this; }
        public BookBuilder publisher(String publisher)     { this.publisher = publisher; return this; }
        public BookBuilder dateReceived(LocalDate d)       { this.dateReceived = d; return this; }
        public BookBuilder availableCopies(int v)          { this.availableCopies = v; return this; }
        public BookBuilder totalCopies(int v)              { this.totalCopies = v; return this; }
        public BookBuilder pages(Integer pages)            { this.pages = pages; return this; }
        public BookBuilder deweyCode(String deweyCode)     { this.deweyCode = deweyCode; return this; }
        public BookBuilder municipalRef(String v)          { this.municipalRef = v; return this; }
        public BookBuilder libraryRef(String v)            { this.libraryRef = v; return this; }

        public Book build() {
            Book b = new Book();
            b.id = this.id; b.title = this.title; b.author = this.author;
            b.isbn = this.isbn; b.category = this.category; b.description = this.description;
            b.coverImage = this.coverImage; b.publisher = this.publisher;
            b.dateReceived = this.dateReceived; b.availableCopies = this.availableCopies;
            b.totalCopies = this.totalCopies; b.pages = this.pages;
            b.deweyCode = this.deweyCode; b.municipalRef = this.municipalRef;
            b.libraryRef = this.libraryRef;
            return b;
        }
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getId()                     { return id; }
    public void setId(Long id)             { this.id = id; }
    public String getTitle()               { return title; }
    public void setTitle(String title)     { this.title = title; }
    public Author getAuthor()              { return author; }
    public void setAuthor(Author author)   { this.author = author; }
    public String getIsbn()               { return isbn; }
    public void setIsbn(String isbn)       { this.isbn = isbn; }
    public String getCategory()            { return category; }
    public void setCategory(String c)      { this.category = c; }
    public String getDescription()         { return description; }
    public void setDescription(String d)   { this.description = d; }
    public String getCoverImage()          { return coverImage; }
    public void setCoverImage(String c)    { this.coverImage = c; }
    public String getPublisher()           { return publisher; }
    public void setPublisher(String p)     { this.publisher = p; }
    public LocalDate getDateReceived()     { return dateReceived; }
    public void setDateReceived(LocalDate d){ this.dateReceived = d; }
    public int getAvailableCopies()        { return availableCopies; }
    public void setAvailableCopies(int v)  { this.availableCopies = v; }
    public int getTotalCopies()            { return totalCopies; }
    public void setTotalCopies(int v)      { this.totalCopies = v; }
    public Integer getPages()              { return pages; }
    public void setPages(Integer pages)    { this.pages = pages; }
    public String getDeweyCode()           { return deweyCode; }
    public void setDeweyCode(String d)     { this.deweyCode = d; }
    public String getMunicipalRef()        { return municipalRef; }
    public void setMunicipalRef(String v)  { this.municipalRef = v; }
    public String getLibraryRef()          { return libraryRef; }
    public void setLibraryRef(String v)    { this.libraryRef = v; }
}
