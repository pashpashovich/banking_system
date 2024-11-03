package ru.clevertec.bank.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.clevertec.bank.entity.enumeration.AccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "accounts")
@AllArgsConstructor
@Data
@NoArgsConstructor
public abstract class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_num")
    private Long accountNum;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    @Column(name = "account_balance")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal accountBalance;
    private String currency;
    @Column(name = "open_date")
    private LocalDate openDate;
    @Column(name = "account_activity")
    private Boolean accountActivity;
    @Column(name = "account_type")
    private AccountType accountType;


    public BigDecimal getPercentage() {
        return accountBalance.multiply(BigDecimal.valueOf(0.01));
    }

    public void withdraw(BigDecimal amount) {
        this.accountBalance = this.accountBalance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.accountBalance = this.accountBalance.add(amount);
    }
}