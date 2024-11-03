package ru.clevertec.bank.request;

import lombok.Data;
import ru.clevertec.bank.domain.CheckingAccountDto;
import ru.clevertec.bank.domain.CreditAccountDto;
import ru.clevertec.bank.domain.SavingsAccountDto;
import ru.clevertec.bank.domain.SocialAccountDto;
import ru.clevertec.bank.entity.enumeration.AccountType;

@Data
public class AccountCreationRequest {
    private AccountType accountType;
    private SavingsAccountDto savingsAccountDto;
    private CheckingAccountDto checkingAccountDto;
    private CreditAccountDto creditAccountDto;
    private SocialAccountDto socialAccountDto;
}
