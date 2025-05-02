package by.bsuir.bank.repository;

import by.bsuir.bank.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  User findByLogin(String login);

  boolean existsUserByLogin(String login);

  boolean existsUserByEmail(String email);

  Optional<User> findByEmail(String email);
}
