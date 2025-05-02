package by.bsuir.bank.entity;

import by.bsuir.bank.entity.enumeration.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CheckingAccount extends Account {

  @Column(name = "overdraft_limit")
  private BigDecimal overdraftLimit;

  public CheckingAccount(Long accountNum, Client client, BigDecimal accountBalance, String currency, LocalDate openDate,
      Boolean accountActivity, BigDecimal overdraftLimit) {
    super(accountNum, client, accountBalance, currency, openDate, accountActivity, AccountType.CHECKING);
    this.overdraftLimit = overdraftLimit;
  }

  @Override
  public void withdraw(BigDecimal amount) {
    if (getAccountBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0
        && getAccountBalance().subtract(amount).abs().compareTo(overdraftLimit) > 0) {
      throw new IllegalArgumentException("Insufficient funds and exceeded overdraft limit");
    }
    super.withdraw(amount);
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
    CheckingAccount that = (CheckingAccount) o;
    return getAccountNum() != null && Objects.equals(getAccountNum(), that.getAccountNum());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
        .hashCode() : getClass().hashCode();
  }
}
