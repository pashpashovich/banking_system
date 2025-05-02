package by.bsuir.bank.domain;

import lombok.Data;
import by.bsuir.bank.entity.enumeration.AccountType;

@Data
public class AccountCreationRequest {
    private AccountType accountType;
    private SavingsAccountDto savingsAccountDto;
    private CheckingAccountDto checkingAccountDto;
    private CreditAccountDto creditAccountDto;
    private SocialAccountDto socialAccountDto;
}
