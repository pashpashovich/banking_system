package ru.clevertec.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

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
