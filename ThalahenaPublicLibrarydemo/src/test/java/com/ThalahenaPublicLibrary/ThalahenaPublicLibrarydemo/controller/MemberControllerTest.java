package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc REST API tests for MemberController (admin member CRUD).
 * UserRepository is mocked so no database is touched.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User sampleMember() {
        User user = User.builder().id(1L).username("member1").email("member1@gmail.com").role(Role.MEMBER).build();
        return user;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllMembers_returnsList() throws Exception {
        when(userRepository.findAllMembers()).thenReturn(java.util.List.of(sampleMember()));

        mockMvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllMembers_staffRole_isForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/members").with(
                        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("staff1").roles("STAFF")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createMember_duplicateUsername_returns400() throws Exception {
        when(userRepository.existsByUsername("member1")).thenReturn(true);

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "username", "member1", "email", "new@gmail.com", "password", "Passw0rd123"));

        mockMvc.perform(post("/api/admin/members")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMember_notFound_returns404() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/admin/members/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMember_notAMember_returns400() throws Exception {
        User staff = User.builder().id(2L).username("staff1").role(Role.STAFF).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(staff));

        mockMvc.perform(delete("/api/admin/members/2"))
                .andExpect(status().isBadRequest());

        verify(userRepository, never()).delete(any());
    }

    /**
     * Regression test for a genuine bug found via live testing: deleting a member who still has
     * transaction/fine history violates a DB foreign-key constraint. Before the fix, this
     * uncaught DataIntegrityViolationException fell through to GlobalExceptionHandler's generic
     * Exception handler and returned 500. It must now return 409 Conflict.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteMember_withExistingTransactionHistory_returns409NotInternalServerError() throws Exception {
        User member = sampleMember();
        when(userRepository.findById(1L)).thenReturn(Optional.of(member));
        doThrow(new DataIntegrityViolationException("FK constraint violation"))
                .when(userRepository).delete(member);

        mockMvc.perform(delete("/api/admin/members/1"))
                .andExpect(status().isConflict());
    }
}
