package ru.clevertec.bank.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.bank.entity.enumeration.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Data
public class CreditAccount extends Account {

    private BigDecimal creditLimit;

    public CreditAccount(Long accountNum, Client client, BigDecimal accountBalance, String currency, LocalDate openDate, Boolean accountActivity, BigDecimal creditLimit) {
        super(accountNum, client, accountBalance, currency, openDate, accountActivity, AccountType.CREDIT);
        this.creditLimit = creditLimit;
    }
}
