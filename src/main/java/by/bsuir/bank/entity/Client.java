package by.bsuir.bank.entity;

import by.bsuir.bank.entity.enumeration.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Getter
@Setter
public class Client extends User {

  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Account> accounts;
  private String address;
  @Column(name = "mobile_phone")
  private String mobilePhone;
  private double income;


  public Client(User user, String address, String mobilePhone, double income) {
    super(user.getId(), user.getLogin(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getSecondName(),
        user.getPatronymicName(), user.getAvatar(), Role.CLIENT, true);
    this.address = address;
    this.income = income;
    this.mobilePhone = mobilePhone;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) {
      return false;
    }
    Client client = (Client) o;
    return getId() != null && Objects.equals(getId(), client.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
        .hashCode() : getClass().hashCode();
  }
}
