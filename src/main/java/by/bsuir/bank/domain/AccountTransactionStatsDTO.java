package by.bsuir.bank.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AccountTransactionStatsDTO {

  private BigDecimal maxTransaction;
  private BigDecimal minTransaction;
  private BigDecimal avgTransaction;
  private BigDecimal totalDeposits;
  private BigDecimal totalWithdrawals;

  public AccountTransactionStatsDTO(BigDecimal maxTransaction, BigDecimal minTransaction, BigDecimal avgTransaction) {
    this.maxTransaction = maxTransaction;
    this.minTransaction = minTransaction;
    this.avgTransaction = avgTransaction;
  }
}
