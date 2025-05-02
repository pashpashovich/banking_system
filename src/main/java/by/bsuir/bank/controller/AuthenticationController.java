package by.bsuir.bank.controller;

import by.bsuir.bank.auth.AuthenticationRequest;
import by.bsuir.bank.auth.AuthenticationResponse;
import by.bsuir.bank.auth.AuthenticationService;
import by.bsuir.bank.auth.RegisterRequest;
import by.bsuir.bank.entity.User;
import by.bsuir.bank.service.PasswordResetService;
import by.bsuir.bank.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.clevertec.annotation.MonitorPerformance;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final PasswordResetService passwordResetService;
  private final UserService userService;


  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
        "error", ex.getReason()
    ));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<?> resetPassword(@RequestParam String email) {
    passwordResetService.resetPassword(email);
    return ResponseEntity.ok("Ссылка для сброса пароля отправлена на ваш email");
  }

  @PostMapping("/confirm-reset")
  public ResponseEntity<?> confirmReset(@RequestBody Map<String, String> payload) {
    String token = payload.get("token");
    String newPassword = payload.get("newPassword");
    if (token == null || newPassword == null) {
      return ResponseEntity.badRequest().body("Токен и новый пароль обязательны");
    }
    User user = passwordResetService.validatePasswordResetToken(token);
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(encodedPassword);
    userService.save(user);
    return ResponseEntity.ok("Пароль успешно изменен");
  }

  @PostMapping("/signUp")
  public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
      if (service.register(request) == null) {
          return ResponseEntity.badRequest().build();
      }
    return ResponseEntity.ok().build();
  }

  @MonitorPerformance
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @MonitorPerformance
  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      SecurityContextHolder.clearContext();
      log.info("User logged out: {}", authentication.getName());
    }
    return ResponseEntity.ok().build();
  }

}
