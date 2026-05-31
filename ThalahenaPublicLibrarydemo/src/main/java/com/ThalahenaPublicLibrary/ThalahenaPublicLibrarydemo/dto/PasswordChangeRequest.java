package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

/**
 * PasswordChangeRequest - DTO representing a request to change password.
 * SRP: Responsibility is to hold old password, new password, and confirmed new password fields.
 */
public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;

    public PasswordChangeRequest() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
