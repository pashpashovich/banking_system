package ru.clevertec.bank.entity;

import jakarta.persistence.Entity;

@Entity
public class SocialAccount extends Account {

    private Boolean socialPayments = true;
}
