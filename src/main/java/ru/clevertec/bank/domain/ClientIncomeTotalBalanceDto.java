package ru.clevertec.bank.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClientIncomeTotalBalanceDto {
    private Long clientId;
    private BigDecimal income;
    private BigDecimal totalBalance;
}
