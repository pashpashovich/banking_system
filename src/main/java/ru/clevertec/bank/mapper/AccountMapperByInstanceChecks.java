package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import ru.clevertec.bank.domain.AccountDto;
import ru.clevertec.bank.domain.CheckingAccountDto;
import ru.clevertec.bank.domain.CreditAccountDto;
import ru.clevertec.bank.domain.SavingsAccountDto;
import ru.clevertec.bank.domain.SocialAccountDto;
import ru.clevertec.bank.entity.*;

@Mapper(componentModel = "spring")
public interface AccountMapperByInstanceChecks {
    CheckingAccountDto map(CheckingAccount account);
    SavingsAccountDto map(SavingsAccount account);
    CreditAccountDto map(CreditAccount account);
    SocialAccountDto map(SocialAccount account);

    CheckingAccount map(CheckingAccountDto accountDto);
    SavingsAccount map(SavingsAccountDto accountDto);
    CreditAccount map(CreditAccountDto accountDto);
    SocialAccount map(SocialAccountDto accountDto);

    default AccountDto mapToAccountDTO(Account account) {
        if (account instanceof CheckingAccount) {
            return map((CheckingAccount) account);
        } else if (account instanceof SavingsAccount) {
            return map((SavingsAccount) account);
        } else if (account instanceof CreditAccount) {
            return map((CreditAccount) account);
        } else if (account instanceof SocialAccount) {
            return map((SocialAccount) account);
        } else {
            return null;
        }
    }

    default Account mapToAccountEntity(AccountDto accountDto) {
        if (accountDto instanceof CheckingAccountDto) {
            return map((CheckingAccountDto) accountDto);
        } else if (accountDto instanceof SavingsAccountDto) {
            return map((SavingsAccountDto) accountDto);
        } else if (accountDto instanceof CreditAccountDto) {
            return map((CreditAccountDto) accountDto);
        } else if (accountDto instanceof SocialAccountDto) {
            return map((SocialAccountDto) accountDto);
        } else {
            return null;
        }
    }
}

