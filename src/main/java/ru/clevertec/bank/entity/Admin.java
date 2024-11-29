package ru.clevertec.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;
import ru.clevertec.bank.entity.enumeration.Role;

import java.util.Objects;

@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Admin extends User {
    @Column(name = "mobile_phone")
    private String mobilePhone;


    public Admin(User user,String mobilePhone) {
        super(user.getId(), user.getLogin(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getSecondName(), user.getPatronymicName(), user.getAvatar(), Role.ADMIN,true);
        this.mobilePhone = mobilePhone;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Client client = (Client) o;
        return getId() != null && Objects.equals(getId(), client.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
