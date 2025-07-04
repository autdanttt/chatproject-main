package com.forcy.chatapp.auth;

import com.forcy.chatapp.PasswordResetTokenRepository;
import com.forcy.chatapp.auth.dto.ResetPasswordByOtpRequest;
import com.forcy.chatapp.auth.exception.OtpInvalidOrExpiredException;
import com.forcy.chatapp.auth.exception.PasswordResetTokenNotFoundException;
import com.forcy.chatapp.entity.PasswordResetToken;
import com.forcy.chatapp.entity.User;
import com.forcy.chatapp.user.UserNotFoundException;
import com.forcy.chatapp.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public String generateOtp(){
        int otp = new Random().nextInt(900000) + 100000; // từ 100000 đến 999999
        return String.valueOf(otp);
    }

    @Transactional
    public void sendPasswordResetOtp(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return; // không báo lỗi để tránh dò email

        String otp = generateOtp();
        PasswordResetToken token = new PasswordResetToken();
        token.setEmail(email);
        token.setToken(otp);

        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + 15 * 60 * 1000); // 15 phút = 15 * 60 * 1000 ms
        token.setExpiresAt(expiresAt);

        passwordResetTokenRepository.save(token);

        // gửi mail
        emailService.sendOtpResetPasswordEmail(email, otp);
    }


    public void resetPasswordByOtp(ResetPasswordByOtpRequest request) {
        PasswordResetToken token = passwordResetTokenRepository
                .findByTokenAndEmail(request.getToken(), request.getEmail())
                .orElseThrow(() -> new PasswordResetTokenNotFoundException("OTP không đúng"));

        if (token.isUsed() || token.getExpiresAt().before(new Date())) {
            throw new OtpInvalidOrExpiredException("OTP đã hết hạn hoặc đã sử dụng");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User không tồn tại"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        passwordResetTokenRepository.delete(token); // 👈 xoá sau khi dùng
    }
}
