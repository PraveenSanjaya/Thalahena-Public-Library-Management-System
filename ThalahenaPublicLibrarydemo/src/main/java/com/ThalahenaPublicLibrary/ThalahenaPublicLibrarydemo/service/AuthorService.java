package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.AuthorDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Author;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Book;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.AuthorRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SRP (Single Responsibility Principle):
 * - This service ONLY handles Author business logic
 * - Validation, DTO conversion, and error handling are centralized here
 * 
 * OCP (Open/Closed Principle):
 * - Can add new methods (e.g., searchAuthors) without modifying existing code
 * - Extension through new methods, not modification
 * 
 * DIP (Dependency Inversion Principle):
 * - Depends on Repository abstractions, not concrete implementations
 * - Spring injects dependencies through constructor
 */
@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private BookRepository bookRepository;

    /**
     * Get all authors with their book information
     */
    public List<AuthorDTO> getAllAuthors() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search authors by name
     */
    public List<AuthorDTO> searchAuthors(String search) {
        List<Author> authors = authorRepository.searchByName(search);
        return authors.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get author by ID
     */
    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
        return convertToDTO(author);
    }

    /**
     * Create new author
     * SRP: Validates and creates author, doesn't handle HTTP concerns
     */
    @Transactional
    public AuthorDTO createAuthor(AuthorDTO authorDTO) {
        // Validation: Author name must not be empty
        if (authorDTO.getName() == null || authorDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty");
        }

        // Validation: Check if author already exists
        if (authorRepository.existsByNameIgnoreCase(authorDTO.getName().trim())) {
            throw new IllegalArgumentException("Author already exists: " + authorDTO.getName());
        }

        // Create and save author
        Author author = Author.builder()
                .name(authorDTO.getName().trim())
                .bio(authorDTO.getBio())
                .build();

        Author savedAuthor = authorRepository.save(author);
        return convertToDTO(savedAuthor);
    }

    /**
     * Update existing author
     */
    @Transactional
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));

        // Validation: Author name must not be empty
        if (authorDTO.getName() == null || authorDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be empty");
        }

        // Update fields
        author.setName(authorDTO.getName().trim());
        author.setBio(authorDTO.getBio());

        Author updatedAuthor = authorRepository.save(author);
        return convertToDTO(updatedAuthor);
    }

    /**
     * Delete author
     * Note: If author has books, this will cascade delete them
     * Consider adding validation to prevent accidental deletion
     */
    @Transactional
    public void deleteAuthor(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with ID: " + id));
        
        // Optional: Check if author has books before deletion
        if (author.getBooks() != null && !author.getBooks().isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete author '" + author.getName() + 
                    "' because they have " + author.getBooks().size() + " book(s). " +
                    "Please reassign or delete the books first."
            );
        }

        authorRepository.delete(author);
    }

    /**
     * Convert Author entity to DTO
     * Includes book information for display
     */
    private AuthorDTO convertToDTO(Author author) {
        // Get book information from the author's books collection
        List<AuthorDTO.BookInfo> bookInfos = author.getBooks() != null ? 
                author.getBooks().stream()
                        .map(book -> new AuthorDTO.BookInfo(
                                book.getId(),
                                book.getTitle()
                        ))
                        .collect(Collectors.toList()) : 
                List.of();

        return AuthorDTO.builder()
                .id(author.getId())
                .name(author.getName())
                .bio(author.getBio())
                .books(bookInfos)
                .build();
    }
}
