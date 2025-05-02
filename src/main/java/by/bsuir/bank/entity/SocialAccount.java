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
public class SocialAccount extends Account {

  @Column(name = "social_payments")
  private Boolean socialPayments = true;

  public SocialAccount(Long accountNum, Client client, BigDecimal accountBalance, String currency, LocalDate openDate,
      Boolean accountActivity, Boolean socialPayments) {
    super(accountNum, client, accountBalance, currency, openDate, accountActivity, AccountType.SOCIAL);
    this.socialPayments = socialPayments;
  }
}
