package ru.clevertec.bank.entity;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class SavingsAccount extends Account {

    private BigDecimal interestRate;
}
