package com.forcy.chatapp.auth;

import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.security.jwt.JwtUtility;
import com.forcy.chatapp.security.jwt.JwtValidationException;
import com.forcy.chatapp.user.UserRepository;
import com.resend.Resend;
import io.jsonwebtoken.Claims;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    private final Resend resend;
    private final JwtUtility jwtUtil;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(@Value("${resend.api.key}") String apiKey,
                        JwtUtility jwtUtil,
                        UserRepository userRepository,
                        JavaMailSender mailSender) {
        this.resend = new Resend(apiKey);
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }
    public void sendVerificationEmail(String to, String token) {
        String subject = "Xác thực tài khoản";
        String link = "http://localhost:10000/api/oauth/verify?token=" + token;
        String html = "<p>Nhấn vào <a href=\"" + link + "\">đây</a> để xác thực tài khoản.</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Chat App");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }

    public void sendOtpResetPasswordEmail(String to, String otpCode) {
        String subject = "Mã xác thực đặt lại mật khẩu";
        String html = "<p>Xin chào,</p>" +
                "<p>Mã xác thực để đặt lại mật khẩu của bạn là:</p>" +
                "<h2>" + otpCode + "</h2>" +
                "<p>Mã này có hiệu lực trong vòng <strong>15 phút</strong>.</p>" +
                "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.</p>" +
                "<br><p>Trân trọng,<br>Chat App</p>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Chat App");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }


    public void verifyEmail(String token) throws JwtValidationException {
        Claims claims = jwtUtil.validateToken(token);

        // Kiểm tra type trong claims
        String type = (String) claims.get("type");
        if (!"email_verification".equals(type)) {
            throw new IllegalArgumentException("Token không hợp lệ cho xác thực email.");
        }

        String email = claims.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        if (user.isVerified()) {
            throw new IllegalStateException("Tài khoản đã được xác thực trước đó.");
        }

        user.setVerified(true);
        userRepository.save(user);
    }
}
