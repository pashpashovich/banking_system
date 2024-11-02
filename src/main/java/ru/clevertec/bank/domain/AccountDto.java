package ru.clevertec.bank.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountDto {
    private Long accountNum;
    private BigDecimal accountBalance;
    private String currency;
    private LocalDate openDate;
    private Boolean accountActivity;
    private Integer clientId;
}
