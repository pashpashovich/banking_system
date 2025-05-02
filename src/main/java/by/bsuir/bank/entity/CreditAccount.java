package by.bsuir.bank.entity;

import by.bsuir.bank.entity.enumeration.AccountType;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class CreditAccount extends Account {

  private BigDecimal creditLimit;

  public CreditAccount(Long accountNum, Client client, BigDecimal accountBalance, String currency, LocalDate openDate,
      Boolean accountActivity, BigDecimal creditLimit) {
    super(accountNum, client, accountBalance, currency, openDate, accountActivity, AccountType.CREDIT);
    this.creditLimit = creditLimit;
  }
}
