package ru.clevertec.bank.request;

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

}
