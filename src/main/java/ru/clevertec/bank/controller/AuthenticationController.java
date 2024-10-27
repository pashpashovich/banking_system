package ru.clevertec.bank.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.bank.auth.AuthenticationRequest;
import ru.clevertec.bank.auth.AuthenticationResponse;
import ru.clevertec.bank.auth.AuthenticationService;
import ru.clevertec.bank.auth.RegisterRequest;


@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/bank/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/signUp")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        if (service.register(request)==null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @GetMapping("/some-endpoint")
    public String someMethod() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Required authority: ADMIN");
        logger.info("User authorities: {}", authentication.getAuthorities());
        return authentication.getAuthorities().toString();
    }
}
