package by.bsuir.bank.entity;

import by.bsuir.bank.entity.enumeration.AccountType;
import jakarta.persistence.Column;
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
public class SavingsAccount extends Account {

  @Column(name = "interest_rate")
  private BigDecimal interestRate;

  public SavingsAccount(Long accountNum, Client client, BigDecimal accountBalance, String currency, LocalDate openDate,
      Boolean accountActivity, BigDecimal interestRate) {
    super(accountNum, client, accountBalance, currency, openDate, accountActivity, AccountType.SAVINGS);
    this.interestRate = interestRate;
  }
}
