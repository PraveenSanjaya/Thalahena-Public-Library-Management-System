package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Author;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Book;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.AuthorRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc REST API tests for BookController.
 * Repositories are mocked (@MockBean) so no database is touched — safe for the shared dev DB.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private AuthorRepository authorRepository;

    private Book sampleBook() {
        Author author = new Author();
        author.setId(1L);
        author.setName("Robert C. Martin");

        Book book = new Book();
        book.setId(1L);
        book.setTitle("Clean Code");
        book.setIsbn("978-0132350884");
        book.setCategory("Software Engineering");
        book.setTotalCopies(3);
        book.setAvailableCopies(3);
        book.setPages(464);
        book.setDeweyCode("005.1");
        book.setAuthor(author);
        return book;
    }

    // ---------- READ (public to any authenticated user) ----------

    @Test
    @WithMockUser(roles = "MEMBER")
    void getAllBooks_returnsList() throws Exception {
        when(bookRepository.findAll()).thenReturn(List.of(sampleBook()));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].isbn").value("978-0132350884"));
    }

    @Test
    void getAllBooks_unauthenticated_isRejected() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getBookById_found_returnsBook() throws Exception {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook()));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author.name").value("Robert C. Martin"));
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void getBookById_notFound_returns404() throws Exception {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound());
    }

    // ---------- CREATE ----------

    @Test
    @WithMockUser(roles = "STAFF")
    void createBook_valid_succeeds() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Martin Fowler");

        when(bookRepository.existsByIsbn("978-0134757599")).thenReturn(false);
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(42L);
            return b;
        });

        mockMvc.perform(multipart("/api/books")
                        .param("title", "Refactoring")
                        .param("authorId", "1")
                        .param("isbn", "978-0134757599")
                        .param("category", "Software Engineering")
                        .param("totalCopies", "2")
                        .param("deweyCode", "005.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.title").value("Refactoring"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void createBook_missingRequiredField_returns400() throws Exception {
        // "title" is a required @RequestParam — Spring rejects the request before hitting the service
        mockMvc.perform(multipart("/api/books")
                        .param("authorId", "1")
                        .param("isbn", "978-0000000000")
                        .param("category", "Fiction")
                        .param("totalCopies", "1")
                        .param("deweyCode", "800"))
                .andExpect(status().isBadRequest());

        verify(bookRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void createBook_duplicateIsbn_isRejectedWithoutSaving() throws Exception {
        when(bookRepository.existsByIsbn("978-0132350884")).thenReturn(true);

        mockMvc.perform(multipart("/api/books")
                        .param("title", "Clean Code (duplicate copy)")
                        .param("authorId", "1")
                        .param("isbn", "978-0132350884")
                        .param("category", "Software Engineering")
                        .param("totalCopies", "1")
                        .param("deweyCode", "005.1"))
                .andExpect(status().isBadRequest());

        verify(bookRepository, never()).save(any());
        verify(authorRepository, never()).findById(any());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void createBook_memberRole_isForbidden() throws Exception {
        mockMvc.perform(multipart("/api/books")
                        .param("title", "Some Book")
                        .param("authorId", "1")
                        .param("isbn", "978-1111111111")
                        .param("category", "Fiction")
                        .param("totalCopies", "1")
                        .param("deweyCode", "800"))
                .andExpect(status().isForbidden());

        verify(bookRepository, never()).save(any());
    }

    // ---------- UPDATE ----------

    @Test
    @WithMockUser(roles = "STAFF")
    void updateBook_valid_succeeds() throws Exception {
        Book existing = sampleBook();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existing.getAuthor()));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/books/1")
                        .param("title", "Clean Code (2nd Edition)")
                        .param("authorId", "1")
                        .param("isbn", "978-0132350884")
                        .param("category", "Software Engineering")
                        .param("totalCopies", "4")
                        .param("deweyCode", "005.1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Clean Code (2nd Edition)"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void updateBook_nonexistentId_returns404() throws Exception {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/books/999")
                        .param("title", "Ghost Book")
                        .param("authorId", "1")
                        .param("isbn", "978-2222222222")
                        .param("category", "Fiction")
                        .param("totalCopies", "1")
                        .param("deweyCode", "800"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void updateBook_changingIsbnToAnotherBooksIsbn_isRejected() throws Exception {
        Book existing = sampleBook(); // isbn = 978-0132350884
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByIsbn("978-9999999999")).thenReturn(true);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/books/1")
                        .param("title", "Clean Code")
                        .param("authorId", "1")
                        .param("isbn", "978-9999999999")
                        .param("category", "Software Engineering")
                        .param("totalCopies", "3")
                        .param("deweyCode", "005.1"))
                .andExpect(status().isBadRequest());

        verify(bookRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void updateBook_keepingSameIsbn_isAllowed() throws Exception {
        Book existing = sampleBook();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(existing.getAuthor()));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/books/1")
                        .param("title", "Clean Code")
                        .param("authorId", "1")
                        .param("isbn", "978-0132350884") // unchanged
                        .param("category", "Software Engineering")
                        .param("totalCopies", "3")
                        .param("deweyCode", "005.1"))
                .andExpect(status().isOk());

        verify(bookRepository, never()).existsByIsbn(anyString());
    }

    // ---------- DELETE ----------

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_valid_succeeds() throws Exception {
        Book existing = sampleBook();
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isOk());

        verify(bookRepository, times(1)).delete(existing);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_nonexistent_returns404() throws Exception {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/books/999"))
                .andExpect(status().isNotFound());

        verify(bookRepository, never()).delete(any());
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void deleteBook_memberRole_isForbidden() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isForbidden());

        verify(bookRepository, never()).delete(any());
    }
}
