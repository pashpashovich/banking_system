package ru.clevertec.bank.entity;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class CheckingAccount extends Account {

    private BigDecimal overdraftLimit;

    @Override
    public void withdraw(BigDecimal amount) {
        if (getAccountBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0
                && getAccountBalance().subtract(amount).abs().compareTo(overdraftLimit) > 0) {
            throw new IllegalArgumentException("Insufficient funds and exceeded overdraft limit");
        }
        super.withdraw(amount);
    }
}
