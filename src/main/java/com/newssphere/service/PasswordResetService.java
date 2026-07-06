package com.newssphere.service;

import com.newssphere.model.PasswordResetToken;
import com.newssphere.model.User;
import com.newssphere.repository.PasswordResetTokenRepository;
import com.newssphere.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Bước 1: tạo token và gửi mail
    @Transactional
    public void initiateReset(String email, String baseUrl) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        // Luôn trả về thành công dù email có tồn tại hay không
        // (tránh leak thông tin user)
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();

        // Xóa token cũ nếu có
        tokenRepository.deleteByUserId(user.getId());

        // Tạo token mới
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        // Gửi mail
        String resetLink = baseUrl + "/auth/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
    }

    // Bước 2: validate token
    public Optional<PasswordResetToken> validateToken(String token) {
        return tokenRepository.findByToken(token)
                .filter(t -> !t.isUsed() && !t.isExpired());
    }

    // Bước 3: đổi password
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = validateToken(token);
        if (tokenOpt.isEmpty()) return false;

        PasswordResetToken resetToken = tokenOpt.get();
        User user = resetToken.getUser();

        // Cập nhật password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Đánh dấu token đã dùng
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }
}
