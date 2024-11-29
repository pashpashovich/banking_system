package ru.clevertec.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.TransactionDTO;
import ru.clevertec.bank.entity.enumeration.TransactionType;
import ru.clevertec.bank.domain.AccountTransactionStatsDTO;
import ru.clevertec.bank.domain.DailyTransactionStats;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class StatsService {

    private final TransactionService transactionService;

    private final CurrencyConversionService currencyConversionService;

    @Autowired
    public StatsService(TransactionService transactionService, CurrencyConversionService currencyConversionService) {
        this.transactionService = transactionService;
        this.currencyConversionService = currencyConversionService;
    }


    public Map<Integer, DailyTransactionStats> getAccountDailyTransactionStats(int month, String accountNum)
    {
        LocalDateTime startDate = LocalDate.now().withMonth(month).withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        List<TransactionDTO> transactions = transactionService.findTransactionsByDateRangeAndAccount(
                startDate, endDate, accountNum);
        Map<Integer, DailyTransactionStats> dailyStats = new HashMap<>();
        for (TransactionDTO transaction : transactions) {
            int day = transaction.getTransactionTime().getDayOfMonth();
            dailyStats.putIfAbsent(day, new DailyTransactionStats(day));
            DailyTransactionStats stats = dailyStats.get(day);
            BigDecimal amountInByn = currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(), "BYN");
            if (transaction.getTransactionType().equals(TransactionType.DEPOSIT) || (transaction.getTransactionType().equals(TransactionType.TRANSFER) && Objects.equals(transaction.getRecipientAccountId(), Long.valueOf(accountNum)))) {
                stats.addDeposit(amountInByn);
            } else if (transaction.getTransactionType().equals(TransactionType.WITHDRAWAL) || (transaction.getTransactionType().equals(TransactionType.TRANSFER) && Objects.equals(transaction.getSenderAccountId(), Long.valueOf(accountNum)))) {
                stats.addWithdrawal(amountInByn);
            }
        }
        return dailyStats;
    }

    public AccountTransactionStatsDTO getAccountTransactionStats(String accountNum, int month) {

        int year = Year.now().getValue();
        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23,59,59);

        List<TransactionDTO> transactions = transactionService.findTransactionsByDateRangeAndAccount(
                startDate, endDate, accountNum);

        List<BigDecimal> transactionsInByn = transactions.stream()
                .map(transaction -> currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(), "BYN"))
                .toList();

        BigDecimal maxTransaction = transactionsInByn.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal minTransaction = transactionsInByn.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal avgTransaction = transactionsInByn.isEmpty() ? BigDecimal.ZERO :
                transactionsInByn.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(transactionsInByn.size()), BigDecimal.ROUND_HALF_UP);

        BigDecimal totalDepositInByn = transactionService.findDepositTransactions(accountNum, startDate, endDate)
                .stream()
                .map(transaction -> currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(), "BYN"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawalInByn = transactionService.findWithdrawalTransactions(accountNum, startDate, endDate)
                .stream()
                .map(transaction -> currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(), "BYN"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        AccountTransactionStatsDTO stats = new AccountTransactionStatsDTO(
                maxTransaction, minTransaction, avgTransaction, totalDepositInByn, totalWithdrawalInByn
        );
        return stats;
    }

    public AccountTransactionStatsDTO getTransactionStats(LocalDate startDate1, LocalDate endDate1) {
        LocalDateTime startDate = startDate1.atStartOfDay();
        LocalDateTime endDate = endDate1.atTime(23,59,59);
        List<TransactionDTO> transactions = transactionService.findTransactionsByDateRange(
                startDate, endDate);
        List<BigDecimal> transactionsInByn = transactions.stream()
                .map(transaction -> currencyConversionService.convert(BigDecimal.valueOf(transaction.getAmount()), transaction.getCurrency(), "BYN"))
                .toList();
        BigDecimal maxTransaction = transactionsInByn.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal minTransaction = transactionsInByn.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal avgTransaction = transactionsInByn.isEmpty() ? BigDecimal.ZERO :
                transactionsInByn.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(transactionsInByn.size()), BigDecimal.ROUND_HALF_UP);

        AccountTransactionStatsDTO stats = new AccountTransactionStatsDTO(maxTransaction, minTransaction, avgTransaction);
        return stats;
    }
}
