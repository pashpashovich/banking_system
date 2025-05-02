package by.bsuir.bank.service;

import by.bsuir.bank.entity.PasswordResetToken;
import by.bsuir.bank.entity.User;
import by.bsuir.bank.repository.PasswordResetTokenRepository;
import by.bsuir.bank.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

  private final UserRepository userRepository;
  private final PasswordResetTokenRepository tokenRepository;
  private final EmailService emailService;

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
    String message = "Для сброса пароля перейдите по ссылке: " + resetLink
        + "\n Если не вы запрашивали восстановление пароля, то игнорируйте это письмо";
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
