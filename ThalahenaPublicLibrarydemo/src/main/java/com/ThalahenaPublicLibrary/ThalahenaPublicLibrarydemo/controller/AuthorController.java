package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.AuthorDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.MessageResponse;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Author CRUD operations
 * 
 * SRP: Only handles HTTP request/response, delegates to AuthorService
 * OCP: Can add new endpoints without modifying existing ones
 * DIP: Depends on AuthorService abstraction, not concrete implementation
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/staff/authors")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class AuthorController {
    
    @Autowired
    private AuthorService authorService;

    /**
     * GET /api/staff/authors
     * Get all authors with their book information
     * Optional: ?search=query to filter by name
     */
    @GetMapping
    public ResponseEntity<List<AuthorDTO>> getAllAuthors(
            @RequestParam(required = false) String search) {
        List<AuthorDTO> authors;
        
        if (search != null && !search.isEmpty()) {
            authors = authorService.searchAuthors(search);
        } else {
            authors = authorService.getAllAuthors();
        }
        
        return ResponseEntity.ok(authors);
    }

    /**
     * GET /api/staff/authors/{id}
     * Get single author by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable Long id) {
        AuthorDTO author = authorService.getAuthorById(id);
        return ResponseEntity.ok(author);
    }

    /**
     * POST /api/staff/authors
     * Create new author
     * Request body: { "name": "Author Name", "bio": "Biography" }
     */
    @PostMapping
    public ResponseEntity<?> createAuthor(@RequestBody AuthorDTO authorDTO) {
        try {
            AuthorDTO createdAuthor = authorService.createAuthor(authorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAuthor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error creating author: " + e.getMessage()));
        }
    }

    /**
     * PUT /api/staff/authors/{id}
     * Update existing author
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAuthor(
            @PathVariable Long id,
            @RequestBody AuthorDTO authorDTO) {
        try {
            AuthorDTO updatedAuthor = authorService.updateAuthor(id, authorDTO);
            return ResponseEntity.ok(updatedAuthor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating author: " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/staff/authors/{id}
     * Delete author (only if they have no books)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id) {
        try {
            authorService.deleteAuthor(id);
            return ResponseEntity.ok()
                    .body(new MessageResponse("Author deleted successfully"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error deleting author: " + e.getMessage()));
        }
    }
}
