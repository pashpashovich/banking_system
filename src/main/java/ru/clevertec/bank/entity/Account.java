package ru.clevertec.bank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "accounts")
@Data
public class Account implements Serializable {
    @Id
    @Column(name = "account_number")
    private Long accountNumber;
    private double balance;
    @Column(name = "open_date")
    private Date openDate;
}