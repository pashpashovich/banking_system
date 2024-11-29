package ru.clevertec.bank.domain;

import java.math.BigDecimal;

public class DailyTransactionStats {
    private int day;
    private BigDecimal deposits;
    private BigDecimal withdrawals;

    public DailyTransactionStats(int day) {
        this.day = day;
        this.deposits=BigDecimal.ZERO;
        this.withdrawals=BigDecimal.ZERO;
    }

    public void addDeposit(BigDecimal amount) {
        this.deposits = this.deposits.add(amount);
    }

    public void addWithdrawal(BigDecimal amount) {
        this.withdrawals = this.withdrawals.add(amount);
    }

    public int getDay() {
        return day;
    }

    public double getDeposits() {
        return deposits.doubleValue();
    }

    public double getWithdrawals() {
        return withdrawals.doubleValue();
    }
}