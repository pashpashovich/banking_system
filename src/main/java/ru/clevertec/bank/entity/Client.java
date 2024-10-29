package ru.clevertec.bank.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.bank.entity.enumeration.Role;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Client extends User {
    @OneToMany
    private List<Account> accounts;

    public Client(Long id, String login, String email, String password, String firstName, String secondName, String patronymicName) {
        super(id, login, email, password, firstName, secondName, patronymicName, Role.CLIENT);
    }

    public Client(User user) {
        super(user.getId(), user.getLogin(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getSecondName(), user.getPatronymicName(), Role.CLIENT);
    }
}
