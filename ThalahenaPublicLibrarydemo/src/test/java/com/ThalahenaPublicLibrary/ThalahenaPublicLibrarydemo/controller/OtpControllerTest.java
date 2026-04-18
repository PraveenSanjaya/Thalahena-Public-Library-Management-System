package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.controller;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.OtpRequest;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto.OtpVerificationRequest;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.Role;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.User;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.UserRepository;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.security.JwtUtils;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OtpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OtpService otpService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testRequestOtp_Success() throws Exception {
        String email = "test@gmail.com";
        OtpRequest request = new OtpRequest();
        request.setEmail(email);

        User mockUser = new User();
        mockUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        mockMvc.perform(post("/api/auth/request-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent to your email."));
    }

    @Test
    public void testRequestOtp_EmailNotFound() throws Exception {
        String email = "notfound@gmail.com";
        OtpRequest request = new OtpRequest();
        request.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/request-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: No account found with this email!"));
    }

    @Test
    public void testVerifyOtp_Success() throws Exception {
        String email = "test@gmail.com";
        String otp = "123456";
        OtpVerificationRequest request = new OtpVerificationRequest();
        request.setEmail(email);
        request.setOtp(otp);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail(email);
        mockUser.setRole(Role.MEMBER);

        when(otpService.verifyOtp(email, otp)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("mock-jwt-token");

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    public void testVerifyOtp_InvalidOtp() throws Exception {
        String email = "test@gmail.com";
        String otp = "000000";
        OtpVerificationRequest request = new OtpVerificationRequest();
        request.setEmail(email);
        request.setOtp(otp);

        when(otpService.verifyOtp(email, otp)).thenReturn(false);

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Invalid or expired OTP!"));
    }
}
