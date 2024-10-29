package ru.clevertec.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.clevertec.bank.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    public User findByLogin(String login);

    public boolean existsUserByLogin(String login);
    public boolean existsUserByEmail(String email);

}
