package by.bsuir.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.bank.entity.PasswordResetToken;
import by.bsuir.bank.entity.User;
import by.bsuir.bank.repository.PasswordResetTokenRepository;
import by.bsuir.bank.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PasswordResetServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordResetTokenRepository tokenRepository;

  @Mock
  private EmailService emailService;

  @InjectMocks
  private PasswordResetService passwordResetService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldCreateNewResetTokenWhenNotExists() {
    // Given
    User user = new User();
    user.setEmail("test@example.com");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

    // When
    passwordResetService.resetPassword("test@example.com");

    // Then
    verify(tokenRepository).save(any(PasswordResetToken.class));
    verify(emailService).sendEmail(eq("test@example.com"), eq("Reset Password"),
        contains("http://localhost:3000/reset-password?token="));
  }

  @Test
  void shouldUpdateExistingResetToken() {
    // Given
    User user = new User();
    user.setEmail("test@example.com");

    PasswordResetToken existingToken = new PasswordResetToken();
    existingToken.setUser(user);

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(tokenRepository.findByUser(user)).thenReturn(Optional.of(existingToken));

    // When
    passwordResetService.resetPassword("test@example.com");

    // Then
    verify(tokenRepository).save(existingToken);
    assertNotNull(existingToken.getToken());
    assertNotNull(existingToken.getExpiryDate());
    verify(emailService).sendEmail(eq("test@example.com"), eq("Reset Password"),
        contains("http://localhost:3000/reset-password?token="));
  }

  @Test
  void shouldThrowExceptionWhenUserNotFoundDuringReset() {
    // Given
    when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    // When / Then
    assertThrows(IllegalArgumentException.class, () -> passwordResetService.resetPassword("unknown@example.com"));
  }

  @Test
  void shouldValidateValidToken() {
    // Given
    User user = new User();
    PasswordResetToken token = new PasswordResetToken();
    token.setToken("valid-token");
    token.setExpiryDate(LocalDateTime.now().plusMinutes(30));
    token.setUser(user);

    when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));

    // When
    User result = passwordResetService.validatePasswordResetToken("valid-token");

    // Then
    assertEquals(user, result);
  }

  @Test
  void shouldThrowExceptionWhenTokenIsExpired() {
    // Given
    PasswordResetToken token = new PasswordResetToken();
    token.setToken("expired-token");
    token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

    when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(token));

    // When / Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> passwordResetService.validatePasswordResetToken("expired-token"));
    assertEquals("Токен истек", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenTokenIsInvalid() {
    // Given
    when(tokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

    // When / Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> passwordResetService.validatePasswordResetToken("invalid-token"));
    assertEquals("Неверный или истекший токен", exception.getMessage());
  }

  @Test
  void shouldDeleteTokenByUserId() {
    // Given
    User user = new User();
    PasswordResetToken token = new PasswordResetToken();
    token.setUser(user);

    when(tokenRepository.findByUser(user)).thenReturn(Optional.of(token));

    // When
    passwordResetService.deleteByUserId(user);

    // Then
    verify(tokenRepository).delete(token);
  }

  @Test
  void shouldNotThrowIfTokenDoesNotExistOnDelete() {
    // Given
    User user = new User();
    when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

    // When
    passwordResetService.deleteByUserId(user);

    // Then
    verify(tokenRepository, never()).delete(any());
  }
}
