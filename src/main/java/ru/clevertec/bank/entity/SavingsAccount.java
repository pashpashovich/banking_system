package ru.clevertec.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.bank.entity.enumeration.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
public class SavingsAccount extends Account {

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    public SavingsAccount(Long accountNum, Client client, BigDecimal accountBalance, String currency, LocalDate openDate, Boolean accountActivity, BigDecimal interestRate) {
        super(accountNum, client, accountBalance, currency, openDate, accountActivity, AccountType.SAVINGS);
        this.interestRate = interestRate;
    }
}
