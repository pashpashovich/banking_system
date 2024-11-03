package ru.clevertec.bank.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.clevertec.bank.entity.enumeration.AccountType;

import java.time.LocalDate;

@Data
public class AccountDto {
    private Long accountNum;
    private double accountBalance;
    private String currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate openDate;
    private Boolean accountActivity;
    private Long clientId;
    private AccountType accountType;

    public AccountType getAccountType() {
        return accountType;
    }
}
