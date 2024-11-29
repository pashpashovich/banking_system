package ru.clevertec.bank.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;
import ru.clevertec.bank.entity.enumeration.Role;

import java.util.List;
import java.util.Objects;

@Entity
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Client extends User {
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;
    private String address;
    @Column(name = "mobile_phone")
    private String mobilePhone;
    private double income;


    public Client(User user, String address, String mobilePhone, double income) {
        super(user.getId(), user.getLogin(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getSecondName(), user.getPatronymicName(), user.getAvatar(), Role.CLIENT,true);
        this.address = address;
        this.income = income;
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
