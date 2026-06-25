package com.newssphere.controller;

import com.newssphere.service.UserService;
import com.newssphere.dto.RegisterDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // =============================================
    // GET /auth/login — hiển thị trang login
    // =============================================
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("errorMsg", "Email hoặc mật khẩu không đúng.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Bạn đã đăng xuất thành công.");
        }
        // Trả về trang auth duy nhất (có cả login + register tab)
        model.addAttribute("activeTab", "login");
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/auth";
    }

    // =============================================
    // GET /auth/register — chuyển sang tab register
    // =============================================
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("activeTab", "register");
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/auth";
    }

    // =============================================
    // POST /auth/register — xử lý đăng ký
    // =============================================
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerDTO") RegisterDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // Kiểm tra validation errors
        if (result.hasErrors()) {
            model.addAttribute("activeTab", "register");
            return "auth/auth";
        }

        // Kiểm tra email đã tồn tại
        if (userService.existsByEmail(dto.getEmail())) {
            model.addAttribute("activeTab", "register");
            model.addAttribute("registerError", "Email này đã được sử dụng.");
            return "auth/auth";
        }

        try {
            userService.register(dto);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Tạo tài khoản thành công! Hãy đăng nhập.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            model.addAttribute("activeTab", "register");
            model.addAttribute("registerError", "Có lỗi xảy ra. Vui lòng thử lại.");
            return "auth/auth";
        }
    }
}
