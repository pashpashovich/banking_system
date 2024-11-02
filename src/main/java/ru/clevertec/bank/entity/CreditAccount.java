package ru.clevertec.bank.entity;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class CreditAccount extends Account {

    private BigDecimal creditLimit;
}
