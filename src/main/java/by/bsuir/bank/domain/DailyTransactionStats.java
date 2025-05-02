package by.bsuir.bank.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DailyTransactionStats {

  public DailyTransactionStats(int day) {
    this.day = day;
    this.deposits = BigDecimal.ZERO;
    this.withdrawals = BigDecimal.ZERO;
  }

  @Getter
  private int day;
  private BigDecimal deposits;
  private BigDecimal withdrawals;

  public void addDeposit(BigDecimal amount) {
    this.deposits = this.deposits.add(amount);
  }

  public void addWithdrawal(BigDecimal amount) {
    this.withdrawals = this.withdrawals.add(amount);
  }

}
