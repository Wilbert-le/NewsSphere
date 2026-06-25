package com.newssphere.controller;

import com.newssphere.model.PasswordResetToken;
import com.newssphere.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    // =============================================
    // GET /auth/forgot-password — form nhập email
    // =============================================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    // =============================================
    // POST /auth/forgot-password — xử lý gửi mail
    // =============================================
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort();

        try {
            passwordResetService.initiateReset(email, baseUrl);
        } catch (Exception e) {
            // Không báo lỗi kỹ thuật ra ngoài
        }

        // Luôn hiện thông báo thành công (tránh leak email)
        redirectAttributes.addFlashAttribute("successMsg",
                "Nếu email tồn tại, chúng tôi đã gửi link reset password. Hãy kiểm tra hộp thư.");
        return "redirect:/auth/forgot-password";
    }

    // =============================================
    // GET /auth/reset-password?token=xxx
    // =============================================
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> tokenOpt = passwordResetService.validateToken(token);
        if (tokenOpt.isEmpty()) {
            model.addAttribute("errorMsg", "Link đã hết hạn hoặc không hợp lệ.");
            return "auth/reset-password-invalid";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    // =============================================
    // POST /auth/reset-password — lưu password mới
    // =============================================
    @PostMapping("/reset-password")
    public String handleResetPassword(
            @RequestParam String token,
            @RequestParam @NotBlank @Size(min = 8) String newPassword,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        boolean success = passwordResetService.resetPassword(token, newPassword);
        if (!success) {
            model.addAttribute("token", token);
            model.addAttribute("errorMsg", "Link đã hết hạn. Vui lòng thử lại.");
            return "auth/reset-password";
        }

        redirectAttributes.addFlashAttribute("successMsg",
                "Đổi mật khẩu thành công! Hãy đăng nhập.");
        return "redirect:/auth/login";
    }
}
