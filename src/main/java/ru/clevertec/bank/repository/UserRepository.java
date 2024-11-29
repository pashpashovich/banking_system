package ru.clevertec.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.bank.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByLogin(String login);

    boolean existsUserByLogin(String login);
    boolean existsUserByEmail(String email);

    Optional<User> findByEmail(String email);
}
