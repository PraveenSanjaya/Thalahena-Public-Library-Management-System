package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.service;

import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.OtpToken;
import com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.repository.OtpTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OtpServiceTest {

    @InjectMocks
    private OtpService otpService;

    @Mock
    private OtpTokenRepository otpRepository;

    @Mock
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateAndSendOtp_Success() {
        String email = "test@gmail.com";

        otpService.generateAndSendOtp(email);

        verify(otpRepository, times(1)).save(any(OtpToken.class));
        verify(emailService, times(1)).sendEmail(eq(email), anyString(), anyString());
    }

    @Test
    public void verifyOtp_ValidOtp() {
        String email = "test@gmail.com";
        String otp = "123456";

        OtpToken mockToken = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5)) // Valid time
                .used(false)
                .build();

        when(otpRepository.findFirstByEmailAndUsedFalseOrderByExpiryTimeDesc(email))
                .thenReturn(Optional.of(mockToken));

        boolean isValid = otpService.verifyOtp(email, otp);

        assertTrue(isValid);
        assertTrue(mockToken.isUsed()); // Should mark as used
        verify(otpRepository, times(1)).save(mockToken);
    }

    @Test
    public void verifyOtp_InvalidOtp() {
        String email = "test@gmail.com";

        OtpToken mockToken = OtpToken.builder()
                .email(email)
                .otp("123456")
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();

        when(otpRepository.findFirstByEmailAndUsedFalseOrderByExpiryTimeDesc(email))
                .thenReturn(Optional.of(mockToken));

        boolean isValid = otpService.verifyOtp(email, "654321"); // Wrong OTP

        assertFalse(isValid);
        assertFalse(mockToken.isUsed());
    }

    @Test
    public void verifyOtp_ExpiredOtp() {
        String email = "test@gmail.com";
        String otp = "123456";

        OtpToken mockToken = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiryTime(LocalDateTime.now().minusMinutes(5)) // Expired time
                .used(false)
                .build();

        when(otpRepository.findFirstByEmailAndUsedFalseOrderByExpiryTimeDesc(email))
                .thenReturn(Optional.of(mockToken));

        boolean isValid = otpService.verifyOtp(email, otp);

        assertFalse(isValid);
        assertFalse(mockToken.isUsed());
    }
}
