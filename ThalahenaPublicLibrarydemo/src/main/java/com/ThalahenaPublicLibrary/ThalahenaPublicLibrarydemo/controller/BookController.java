package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.BookDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Author;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Book;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.AuthorRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff/books")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class BookController {
    @Autowired
    BookRepository bookRepository;
    
    @Autowired
    AuthorRepository authorRepository;

    @Value("${upload.path:C:/Users/Praveen/Downloads}")
    private String uploadBasePath;
    
    private final String uploadDir = "/uploads/books/";

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        List<Book> books;
        
        if (search != null && !search.isEmpty()) {
            books = bookRepository.searchBooks(search);
        } else if (category != null && !category.isEmpty()) {
            books = bookRepository.findByCategory(category);
        } else {
            books = bookRepository.findAll();
        }
        
        return ResponseEntity.ok(books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> ResponseEntity.ok(convertToDTO(book)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BookDTO> createBook(
            @RequestParam("title") String title,
            @RequestParam("authorId") Long authorId,
            @RequestParam("isbn") String isbn,
            @RequestParam("category") String category,
            @RequestParam("totalCopies") int totalCopies,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "dateReceived", required = false) String dateReceived,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setCategory(category);
        book.setPublisher(publisher);
        book.setDescription(description);
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(totalCopies);
        
        if (dateReceived != null && !dateReceived.isEmpty()) {
            book.setDateReceived(java.time.LocalDate.parse(dateReceived));
        }
        
        // Set author
        Author author = authorRepository.findById(authorId)
            .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
        book.setAuthor(author);
        
        // Handle file upload
        if (file != null && !file.isEmpty()) {
            String fileName = saveFile(file);
            book.setCoverImage(uploadDir + fileName);
        }
        
        Book savedBook = bookRepository.save(book);
        return ResponseEntity.ok(convertToDTO(savedBook));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateBook(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("authorId") Long authorId,
            @RequestParam("isbn") String isbn,
            @RequestParam("category") String category,
            @RequestParam("totalCopies") int totalCopies,
            @RequestParam(value = "publisher", required = false) String publisher,
            @RequestParam(value = "dateReceived", required = false) String dateReceived,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        return bookRepository.findById(id)
                .map(book -> {
                    try {
                        book.setTitle(title);
                        book.setIsbn(isbn);
                        book.setCategory(category);
                        book.setPublisher(publisher);
                        book.setDescription(description);
                        book.setTotalCopies(totalCopies);
                        
                        // Adjust available copies based on difference
                        int oldTotal = book.getTotalCopies();
                        int diff = totalCopies - oldTotal;
                        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + diff));
                        
                        if (dateReceived != null && !dateReceived.isEmpty()) {
                            book.setDateReceived(java.time.LocalDate.parse(dateReceived));
                        }
                        
                        // Set author
                        Author author = authorRepository.findById(authorId)
                            .orElseThrow(() -> new RuntimeException("Author not found with ID: " + authorId));
                        book.setAuthor(author);
                        
                        // Handle new file upload
                        if (file != null && !file.isEmpty()) {
                            // Delete old file if exists
                            if (book.getCoverImage() != null) {
                                deleteFile(book.getCoverImage());
                            }
                            String fileName = saveFile(file);
                            book.setCoverImage(uploadDir + fileName);
                        }
                        
                        return ResponseEntity.ok(convertToDTO(bookRepository.save(book)));
                    } catch (IOException e) {
                        return ResponseEntity.status(500).body("Failed to upload file: " + e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    // Delete cover image file if exists
                    if (book.getCoverImage() != null) {
                        deleteFile(book.getCoverImage());
                    }
                    bookRepository.delete(book);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cover")
    public ResponseEntity<?> uploadCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        Book book = bookRepository.findById(id).orElseThrow();
        
        // Delete old cover if exists
        if (book.getCoverImage() != null) {
            deleteFile(book.getCoverImage());
        }
        
        String fileName = saveFile(file);
        book.setCoverImage(uploadDir + fileName);
        bookRepository.save(book);

        return ResponseEntity.ok(convertToDTO(book));
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadPath = uploadBasePath + uploadDir;
        Path root = Paths.get(uploadPath);
        
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Files.copy(file.getInputStream(), root.resolve(fileName));
        
        return fileName;
    }

    private void deleteFile(String filePath) {
        try {
            // Extract filename from path (remove /uploads/books/ prefix)
            String fileName = filePath.replace(uploadDir, "");
            Path fileToDelete = Paths.get(uploadBasePath + uploadDir + fileName);
            
            if (Files.exists(fileToDelete)) {
                Files.delete(fileToDelete);
            }
        } catch (IOException e) {
            // Log error but don't fail the operation
            System.err.println("Failed to delete file: " + e.getMessage());
        }
    }

    /**
     * Convert Book entity to BookDTO to prevent circular references
     */
    private BookDTO convertToDTO(Book book) {
        BookDTO.AuthorInfo authorInfo = null;
        if (book.getAuthor() != null) {
            authorInfo = BookDTO.AuthorInfo.builder()
                    .id(book.getAuthor().getId())
                    .name(book.getAuthor().getName())
                    .bio(book.getAuthor().getBio())
                    .build();
        }

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .description(book.getDescription())
                .publisher(book.getPublisher())
                .dateReceived(book.getDateReceived())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .coverImage(book.getCoverImage())
                .author(authorInfo)
                .build();
    }
}
