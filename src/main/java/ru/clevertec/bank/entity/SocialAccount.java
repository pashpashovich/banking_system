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
public class SocialAccount extends Account {

    @Column(name = "social_payments")
    private Boolean socialPayments = true;

    public SocialAccount(Long accountNum, Client client, BigDecimal accountBalance, String currency, LocalDate openDate, Boolean accountActivity, Boolean socialPayments) {
        super(accountNum, client, accountBalance, currency, openDate, accountActivity, AccountType.SOCIAL);
        this.socialPayments = socialPayments;
    }
}
