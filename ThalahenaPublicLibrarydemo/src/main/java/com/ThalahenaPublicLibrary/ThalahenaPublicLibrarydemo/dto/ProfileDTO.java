package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ProfileDTO - DTO representing profile information.
 * SRP: Responsibility is purely to act as a data carrier for Profile requests/responses.
 */
public class ProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String role;
    private String profilePicture;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private LocalDate birthDate;
    private String gender;
    private LocalDate membershipDate;
    private String whatsapp;
    private String socialMedia;

    // Staff specific
    private String staffId;
    private String department;
    private String position;

    // Member specific
    private String memberId;
    private Long totalBooksBorrowed;
    private Long currentBorrowedBooks;
    private Double outstandingFines;

    public ProfileDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public String getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public Long getTotalBooksBorrowed() {
        return totalBooksBorrowed;
    }

    public void setTotalBooksBorrowed(Long totalBooksBorrowed) {
        this.totalBooksBorrowed = totalBooksBorrowed;
    }

    public Long getCurrentBorrowedBooks() {
        return currentBorrowedBooks;
    }

    public void setCurrentBorrowedBooks(Long currentBorrowedBooks) {
        this.currentBorrowedBooks = currentBorrowedBooks;
    }

    public Double getOutstandingFines() {
        return outstandingFines;
    }

    public void setOutstandingFines(Double outstandingFines) {
        this.outstandingFines = outstandingFines;
    }
}
