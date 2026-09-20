package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.TransactionDTO;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.BookCondition;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MockMvc REST API tests for TransactionController — issue/return circulation endpoints.
 * TransactionService is mocked (@MockBean) so no database is touched.
 *
 * The one-book-per-member business-rule test below (issueBook_ruleViolation_returns400)
 * is the API-level counterpart of Selenium TEST 6: a rejected issue attempt must surface
 * as HTTP 400 with the business-rule message, per TransactionController's existing
 * catch (IllegalStateException) -> ResponseEntity.badRequest() contract. This is intentional
 * API behavior and must NOT be "fixed" into a 200/201 success.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    private TransactionDTO sampleIssuedTransaction() {
        return TransactionDTO.builder()
                .id(1L).userId(1L).memberName("member1").bookId(10L).bookTitle("Clean Code")
                .issueDate(LocalDate.now()).dueDate(LocalDate.now().plusDays(14))
                .status("ISSUED").fineAmount(0.0).build();
    }

    // ---------- ISSUE ----------

    @Test
    @WithMockUser(roles = "STAFF")
    void issueBook_valid_returns201() throws Exception {
        when(transactionService.issueBook(1L, 10L)).thenReturn(sampleIssuedTransaction());

        mockMvc.perform(post("/api/staff/transactions/issue").param("userId", "1").param("bookId", "10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ISSUED"))
                .andExpect(jsonPath("$.bookTitle").value("Clean Code"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void issueBook_ruleViolation_returns400WithMessage() throws Exception {
        when(transactionService.issueBook(1L, 20L)).thenThrow(
                new IllegalStateException("According to library borrowing rules, a member can borrow only one book at a time. " +
                        "This member currently has 'Clean Code' (Status: ISSUED)."));

        mockMvc.perform(post("/api/staff/transactions/issue").param("userId", "1").param("bookId", "20"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("only one book at a time")));

        // Requirement: rejected issue must not be silently retried/converted into a save elsewhere
        verify(transactionService, times(1)).issueBook(1L, 20L);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void issueBook_bookOrMemberNotFound_returns404() throws Exception {
        when(transactionService.issueBook(404L, 10L)).thenThrow(new RuntimeException("Member not found with ID: 404"));

        mockMvc.perform(post("/api/staff/transactions/issue").param("userId", "404").param("bookId", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void issueBook_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/staff/transactions/issue").param("userId", "1").param("bookId", "10"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(transactionService);
    }

    @Test
    @WithMockUser(roles = "MEMBER")
    void issueBook_memberRole_isForbidden() throws Exception {
        mockMvc.perform(post("/api/staff/transactions/issue").param("userId", "1").param("bookId", "10"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(transactionService);
    }

    // ---------- RETURN ----------

    @Test
    @WithMockUser(roles = "STAFF")
    void returnBook_valid_returns200() throws Exception {
        TransactionDTO returned = TransactionDTO.builder()
                .id(1L).status("RETURNED").fineAmount(0.0).returnDate(LocalDate.now()).build();
        when(transactionService.returnBook(eq(1L), any(LocalDate.class), eq(BookCondition.GOOD), any()))
                .thenReturn(returned);

        mockMvc.perform(put("/api/staff/transactions/return/1")
                        .param("returnDate", LocalDate.now().toString())
                        .param("bookCondition", "GOOD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void returnBook_missingReturnDate_returns400() throws Exception {
        mockMvc.perform(put("/api/staff/transactions/return/1").param("bookCondition", "GOOD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Return Date is required"));

        verifyNoInteractions(transactionService);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void returnBook_missingBookCondition_returns400() throws Exception {
        mockMvc.perform(put("/api/staff/transactions/return/1")
                        .param("returnDate", LocalDate.now().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Book Condition is required"));

        verifyNoInteractions(transactionService);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void returnBook_alreadyReturned_returns400() throws Exception {
        when(transactionService.returnBook(eq(2L), any(LocalDate.class), any(BookCondition.class), any()))
                .thenThrow(new IllegalStateException("This book has already been returned."));

        mockMvc.perform(put("/api/staff/transactions/return/2")
                        .param("returnDate", LocalDate.now().toString())
                        .param("bookCondition", "GOOD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void returnBook_transactionNotFound_returns404() throws Exception {
        when(transactionService.returnBook(eq(999L), any(LocalDate.class), any(BookCondition.class), any()))
                .thenThrow(new RuntimeException("Transaction not found with ID: 999"));

        mockMvc.perform(put("/api/staff/transactions/return/999")
                        .param("returnDate", LocalDate.now().toString())
                        .param("bookCondition", "GOOD"))
                .andExpect(status().isNotFound());
    }

    // ---------- COUNTERS / LIST ----------

    @Test
    @WithMockUser(roles = "STAFF")
    void getAllTransactions_returnsList() throws Exception {
        when(transactionService.getTransactions(null)).thenReturn(java.util.List.of(sampleIssuedTransaction()));

        mockMvc.perform(get("/api/staff/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("ISSUED"));
    }

    @Test
    void getAllTransactions_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/staff/transactions"))
                .andExpect(status().isUnauthorized());
    }
}
