package ru.clevertec.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.clevertec.bank.entity.enumeration.Role;

import java.util.Objects;

@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Director extends User {
    @Column(name = "mobile_phone")
    private String mobilePhone;

    public Director(User user,String mobilePhone) {
        super(user.getId(), user.getLogin(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getSecondName(), user.getPatronymicName(), user.getAvatar(), Role.DIRECTOR,true);
        this.mobilePhone = mobilePhone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Director director)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(getMobilePhone(), director.getMobilePhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMobilePhone());
    }
}
