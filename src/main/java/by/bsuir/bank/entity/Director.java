package by.bsuir.bank.entity;

import by.bsuir.bank.entity.enumeration.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Accessors(chain = true)
@NoArgsConstructor
@Getter
@Setter
public class Director extends User {

  @Column(name = "mobile_phone")
  private String mobilePhone;

  public Director(User user, String mobilePhone) {
    super(user.getId(), user.getLogin(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getSecondName(),
        user.getPatronymicName(), user.getAvatar(), Role.DIRECTOR, true);
    this.mobilePhone = mobilePhone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Director director)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    return Objects.equals(getMobilePhone(), director.getMobilePhone());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getMobilePhone());
  }
}
