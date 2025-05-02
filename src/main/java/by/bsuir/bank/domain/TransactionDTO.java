package by.bsuir.bank.domain;

import by.bsuir.bank.entity.enumeration.TransactionType;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TransactionDTO {

  private Long id;
  private Long senderAccountId;
  private Long recipientAccountId;
  private double amount;
  private String currency;
  private LocalDateTime transactionTime;
  private TransactionType transactionType;
}
