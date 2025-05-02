package by.bsuir.bank.repository;

import by.bsuir.bank.entity.PasswordResetToken;
import by.bsuir.bank.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

  Optional<PasswordResetToken> findByUser(User user);

  Optional<PasswordResetToken> findByToken(String token);


}
