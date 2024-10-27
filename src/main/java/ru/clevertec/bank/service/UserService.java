package ru.clevertec.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.entity.User;
import ru.clevertec.bank.repository.UserRepository;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isLoginAvailable(String login) {
        return !userRepository.existsUserByLogin(login);

    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsUserByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
