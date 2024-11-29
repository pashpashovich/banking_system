package ru.clevertec.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.clevertec.bank.entity.PasswordResetToken;
import ru.clevertec.bank.entity.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser(User user);
    Optional<PasswordResetToken> findByToken(String token);


}