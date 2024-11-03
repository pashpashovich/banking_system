package ru.clevertec.bank.domain;

import lombok.Data;
import ru.clevertec.bank.entity.enumeration.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
