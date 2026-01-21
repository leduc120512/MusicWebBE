//package com.musicapi.service;
//
//import com.musicapi.model.PasswordResetToken;
//import com.musicapi.model.User;
//import com.musicapi.repository.PasswordResetTokenRepository;
//import com.musicapi.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordResetTokenRepository tokenRepository;
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    public String requestPasswordReset(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("Email not found"));
//
//        // Generate a unique token
//        String token = UUID.randomUUID().toString();
//        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24); // Token valid for 24 hours
//
//        // Save the token
//        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
//        tokenRepository.save(resetToken);
//
//        // Send email with reset link
//        String resetLink = "http://localhost:3000/reset-password?token=" + token; // Adjust URL for your React app
//        sendResetEmail(user.getEmail(), resetLink);
//
//        return "Password reset link sent to your email.";
//    }
//
//    private void sendResetEmail(String to, String resetLink) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(to);
//            helper.setSubject("Password Reset Request");
//            helper.setText(
//                    "<h3>Password Reset</h3>" +
//                            "<p>Click the link below to reset your password:</p>" +
//                            "<a href='" + resetLink + "'>Reset Password</a>" +
//                            "<p>This link will expire in 24 hours.</p>", true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email: " + e.getMessage());
//        }
//    }
//
//    public String resetPassword(String token, String newPassword) {
//        PasswordResetToken resetToken = tokenRepository.findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Invalid token"));
//
//        if (resetToken.isExpired()) {
//            throw new RuntimeException("Token has expired");
//        }
//
//        User user = resetToken.getUser();
//        user.setPassword(passwordEncoder.encode(newPassword));
//        userRepository.save(user);
//
//        // Delete the used token
//        tokenRepository.delete(resetToken);
//
//        return "Password reset successfully.";
//    }
//}