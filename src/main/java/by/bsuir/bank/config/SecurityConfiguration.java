package by.bsuir.bank.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtAuthenticationFilter jwtAuthFilter;

  private final AuthenticationProvider authenticationProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            .requestMatchers(HttpMethod.POST, "/api/accounts/create").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/auth/signUp").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/accounts/convert/*/*/*").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/transactions").hasAuthority("CLIENT")
            .requestMatchers(HttpMethod.GET, "/api/transactions/*/*").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/auth/authenticate").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/users/client").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/users/email-check").permitAll()
            .requestMatchers(HttpMethod.DELETE, "/api/users/client/*").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/accounts/").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/transactions/by-date").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/accounts/clients-income-accounts").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/transactions/boxplot").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/transactions/count-by-type").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/confirm-reset").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users/*/avatar").hasAnyAuthority("CLIENT", "ADMIN", "DIRECTOR")
            .requestMatchers(HttpMethod.GET, "/api/users/*/avatar").hasAnyAuthority("CLIENT", "ADMIN", "DIRECTOR")
            .requestMatchers(HttpMethod.GET, "/api/transactions/").hasAnyAuthority("ADMIN", "DIRECTOR")
            .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("DIRECTOR")
            .requestMatchers(HttpMethod.GET, "/api/admin/users").hasAuthority("DIRECTOR")
            .requestMatchers(HttpMethod.PATCH, "/api/admin/users/*").hasAuthority("DIRECTOR")
            .requestMatchers(HttpMethod.POST, "/api/admin/users/*/status").hasAuthority("DIRECTOR")
            .requestMatchers(HttpMethod.DELETE, "/api/admin/users/*").hasAuthority("DIRECTOR")
            .requestMatchers(HttpMethod.GET, "/api/users/client/*").authenticated()
            .requestMatchers(HttpMethod.PATCH, "/api/admin/users/*/role").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/9users/admin/*").hasAnyAuthority("ADMIN", "DIRECTOR")
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/loans").hasAuthority("CLIENT")
            .requestMatchers(HttpMethod.GET, "/api/loans/my").hasAuthority("CLIENT")
            .requestMatchers(HttpMethod.GET, "/api/loan-management").hasAuthority("ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/loan-management/*/status").hasAuthority("ADMIN")
            .anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(authenticationErrorFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public OncePerRequestFilter authenticationErrorFilter() {
    return new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
          FilterChain filterChain) {
        try {
          filterChain.doFilter(request, response);
        } catch (Exception e) {
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.setContentType("application/json");
          try {
            response.getWriter().write("{\"error\": \"Неверный логин или пароль\"}");
          } catch (IOException ex) {
            log.error("Bad..");
          }
        }
      }
    };
  }

}
