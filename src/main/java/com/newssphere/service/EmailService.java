package com.newssphere.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("NewsSphere — Reset your password");
            helper.setFrom("${spring.mail.username}");

            String htmlBody = """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Inter, Arial, sans-serif; background: #f0f4fa; padding: 40px 0;">
                  <div style="max-width: 480px; margin: 0 auto; background: #fff;
                              border-radius: 12px; overflow: hidden;
                              border: 0.5px solid #e2e8f0;">
                    <!-- Header -->
                    <div style="background: #0B2447; padding: 28px 32px;">
                      <span style="font-size: 20px; font-weight: 700; color: #fff;">
                        News<span style="color: #F97316;">Sphere</span>
                      </span>
                    </div>
                    <!-- Body -->
                    <div style="padding: 32px;">
                      <h2 style="font-size: 18px; color: #0f172a; margin-bottom: 12px;">
                        Reset your password
                      </h2>
                      <p style="font-size: 14px; color: #475569; line-height: 1.6; margin-bottom: 24px;">
                        We received a request to reset the password for your NewsSphere account.
                        Click the button below to set a new password. This link will expire in
                        <strong>30 minutes</strong>.
                      </p>
                      <a href="%s"
                         style="display: inline-block; background: #3B82F6; color: #fff;
                                padding: 12px 28px; border-radius: 8px; text-decoration: none;
                                font-size: 14px; font-weight: 500;">
                        Reset Password
                      </a>
                      <p style="font-size: 12px; color: #94a3b8; margin-top: 24px; line-height: 1.6;">
                        If you didn't request this, you can safely ignore this email.
                        Your password will not be changed.
                      </p>
                    </div>
                    <!-- Footer -->
                    <div style="padding: 16px 32px; border-top: 0.5px solid #e2e8f0;">
                      <p style="font-size: 11px; color: #94a3b8;">
                        © 2025 NewsSphere. All rights reserved.
                      </p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(resetLink);

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage(), e);
        }
    }
}
