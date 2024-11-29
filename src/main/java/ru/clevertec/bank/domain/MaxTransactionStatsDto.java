package ru.clevertec.bank.domain;

import lombok.Data;
import ru.clevertec.bank.entity.enumeration.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class MaxTransactionStatsDto {
    private List<LocalDate> dates;
    private List<Double> maxTransfers;
    private List<Double> maxWithdrawals;
    private List<Double> maxDeposits;

    public MaxTransactionStatsDto(Map<LocalDate, Map<TransactionType, Double>> maxTransactionsByDate) {
        this.dates = maxTransactionsByDate.keySet().stream().sorted().toList();
        this.maxTransfers = dates.stream()
                .map(date -> maxTransactionsByDate.getOrDefault(date, Map.of()).getOrDefault(TransactionType.TRANSFER, 0.0))
                .toList();
        this.maxWithdrawals = dates.stream()
                .map(date -> maxTransactionsByDate.getOrDefault(date, Map.of()).getOrDefault(TransactionType.WITHDRAWAL, 0.0))
                .toList();
        this.maxDeposits = dates.stream()
                .map(date -> maxTransactionsByDate.getOrDefault(date, Map.of()).getOrDefault(TransactionType.DEPOSIT, 0.0))
                .toList();
    }

}
