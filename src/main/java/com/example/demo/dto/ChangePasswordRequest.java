package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {
    // Google账号可以不传这个字段
    private String currentPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}