package ru.clevertec.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.bank.entity.PasswordResetToken;
import ru.clevertec.bank.entity.User;
import ru.clevertec.bank.repository.PasswordResetTokenRepository;
import ru.clevertec.bank.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    private UserRepository userRepository;
    private PasswordResetTokenRepository tokenRepository;
    private EmailService emailService;

    public PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String tokenValue = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        Optional<PasswordResetToken> existingToken = tokenRepository.findByUser(user);
        if (existingToken.isPresent()) {
            PasswordResetToken token = existingToken.get();
            token.setToken(tokenValue);
            token.setExpiryDate(expiryDate);
            tokenRepository.save(token);
        } else {
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(tokenValue);
            token.setExpiryDate(expiryDate);
            token.setUser(user);
            tokenRepository.save(token);
        }

        String resetLink = "http://localhost:3000/reset-password?token=" + tokenValue;
        String message = "Для сброса пароля перейдите по ссылке: " + resetLink+ "\n Если не вы запрашивали восстановление пароля, то игнорируйте это письмо";
        emailService.sendEmail(user.getEmail(), "Reset Password", message);
    }

    public User validatePasswordResetToken(String token) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Неверный или истекший токен"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Токен истек");
        }
        return resetToken.getUser();
    }

    public void deleteByUserId(User user) {
        tokenRepository.findByUser(user).ifPresent(resetToken -> tokenRepository.delete(resetToken));
    }
}
