package by.bsuir.bank.entity;

import by.bsuir.bank.entity.enumeration.TransactionType;
import by.bsuir.bank.service.CurrencyConversionService;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_account_id")
  private Account senderAccount;

  @ManyToOne
  @JoinColumn(name = "recipient_account_id")
  private Account recipientAccount;

  private BigDecimal amount;
  private String currency;

  @Column(name = "transaction_time")
  private LocalDateTime transactionTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "transaction_type")
  private TransactionType transactionType;

  public BigDecimal convertAmountTo(BigDecimal amount, CurrencyConversionService currencyConversionService,
      String fromCurrency, String targetCurrency) {
    return currencyConversionService.convert(amount, fromCurrency, targetCurrency);
  }

  public void updateAccountBalances(CurrencyConversionService currencyConversionService) {
    if (TransactionType.TRANSFER.equals(transactionType)) {
      updateTransfer(currencyConversionService);
    } else if (TransactionType.DEPOSIT.equals(transactionType)) {
      updateDeposit(currencyConversionService);
    } else if (TransactionType.WITHDRAWAL.equals(transactionType)) {
      updateWithdrawal(currencyConversionService);
    }
  }

  private void updateTransfer(CurrencyConversionService currencyConversionService) {
    BigDecimal senderAmountInSenderCurrency = convertAmountTo(amount, currencyConversionService, currency,
        senderAccount.getCurrency());
    BigDecimal recipientAmountInRecipientCurrency = convertAmountTo(amount, currencyConversionService, currency,
        recipientAccount.getCurrency());

    senderAccount.setAccountBalance(senderAccount.getAccountBalance().subtract(senderAmountInSenderCurrency));
    recipientAccount.setAccountBalance(recipientAccount.getAccountBalance().add(recipientAmountInRecipientCurrency));
  }

  private void updateDeposit(CurrencyConversionService currencyConversionService) {
    BigDecimal recipientAmountInRecipientCurrency = convertAmountTo(amount, currencyConversionService, currency,
        recipientAccount.getCurrency());
    recipientAccount.setAccountBalance(recipientAccount.getAccountBalance().add(recipientAmountInRecipientCurrency));
  }

  private void updateWithdrawal(CurrencyConversionService currencyConversionService) {
    BigDecimal senderAmountInSenderCurrency = convertAmountTo(amount, currencyConversionService, currency,
        senderAccount.getCurrency());
    senderAccount.setAccountBalance(senderAccount.getAccountBalance().subtract(senderAmountInSenderCurrency));
  }
}
