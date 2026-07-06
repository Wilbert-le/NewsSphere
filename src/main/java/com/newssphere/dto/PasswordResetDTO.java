package com.newssphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordResetDTO {

    // Dùng cho bước 1: nhập email
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    // Dùng cho bước 2: đặt password mới
    @Size(min = 8, message = "Mật khẩu tối thiểu 8 ký tự")
    private String newPassword;

    private String token;

    public PasswordResetDTO() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
