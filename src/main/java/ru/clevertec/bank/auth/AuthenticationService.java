package ru.clevertec.bank.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.config.JwtService;
import ru.clevertec.bank.entity.Client;
import ru.clevertec.bank.entity.User;
import ru.clevertec.bank.entity.enumeration.Role;
import ru.clevertec.bank.repository.UserRepository;
import ru.clevertec.bank.service.ClientService;
import ru.clevertec.bank.service.UserService;

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
        if(userService.isLoginAvailable(request.getLogin()) && userService.isEmailAvailable(request.getEmail()))
        {
            User user = User.builder()
                    .login(request.getLogin())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstName(request.getFirstName())
                    .secondName(request.getSecondName())
                    .patronymicName(request.getPatronymicName())
                    .role(Role.CLIENT)
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByLogin(request.getLogin());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .role(user.getRole().name())
                .build();
    }
}