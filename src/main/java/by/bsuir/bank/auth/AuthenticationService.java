package by.bsuir.bank.auth;


import by.bsuir.bank.config.JwtService;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.entity.User;
import by.bsuir.bank.entity.enumeration.Role;
import by.bsuir.bank.repository.UserRepository;
import by.bsuir.bank.service.ClientService;
import by.bsuir.bank.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final UserService userService;
  private final ClientService clientService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationResponse register(RegisterRequest request) {
    if (userService.isLoginAvailable(request.getLogin()) && userService.isEmailAvailable(request.getEmail())) {
      User user = User.builder()
          .login(request.getLogin())
          .email(request.getEmail())
          .password(passwordEncoder.encode(request.getPassword()))
          .firstName(request.getFirstName())
          .secondName(request.getSecondName())
          .patronymicName(request.getPatronymicName())
          .role(Role.CLIENT)
          .isActive(false)
          .build();
      Client client = new Client(user, request.getAddress(), request.getMobilePhone(), request.getIncome());
      clientService.save(client);
      var jwtToken = jwtService.generateToken(client);
      return AuthenticationResponse.builder()
          .token(jwtToken)
          .build();
    }
    return null;
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getLogin(),
              request.getPassword()
          )
      );

      var user = userRepository.findByLogin(request.getLogin());

      if (!user.isActive()) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Ваш аккаунт заблокирован. Пожалуйста, свяжитесь с поддержкой.");
      }

      var jwtToken = jwtService.generateToken(user);
      return AuthenticationResponse.builder()
          .token(jwtToken)
          .id(user.getId())
          .role(user.getRole().name())
          .build();

    } catch (AuthenticationException ex) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль", ex);
    }
  }

}
