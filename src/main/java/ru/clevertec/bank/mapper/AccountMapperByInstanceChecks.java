package ru.clevertec.bank.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.clevertec.bank.domain.AccountDto;
import ru.clevertec.bank.domain.CheckingAccountDto;
import ru.clevertec.bank.domain.CreditAccountDto;
import ru.clevertec.bank.domain.SavingsAccountDto;
import ru.clevertec.bank.domain.SocialAccountDto;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.CheckingAccount;
import ru.clevertec.bank.entity.Client;
import ru.clevertec.bank.entity.CreditAccount;
import ru.clevertec.bank.entity.SavingsAccount;
import ru.clevertec.bank.entity.SocialAccount;

@Mapper(componentModel = "spring")
public interface AccountMapperByInstanceChecks {
    @Mapping(source = "client", target = "clientId")
    @Mapping(source = "overdraftLimit", target = "overdraftLimit")
    CheckingAccountDto map(CheckingAccount account);


    @Mapping(source = "client", target = "clientId")
    @Mapping(source = "interestRate", target = "interestRate")
    SavingsAccountDto map(SavingsAccount account);

    @Mapping(source = "client", target = "clientId")
    @Mapping(source = "creditLimit", target = "creditLimit")
    CreditAccountDto map(CreditAccount account);

    @Mapping(source = "client", target = "clientId")
    SocialAccountDto map(SocialAccount account);

    @Mapping(source = "overdraftLimit", target = "overdraftLimit")
    CheckingAccount map(CheckingAccountDto accountDto);

    @Mapping(source = "interestRate", target = "interestRate")
    SavingsAccount map(SavingsAccountDto accountDto);

    @Mapping(source = "creditLimit", target = "creditLimit")
    CreditAccount map(CreditAccountDto accountDto);

    SocialAccount map(SocialAccountDto accountDto);

    default Long map(Client client) {
        return client != null ? client.getId() : null;
    }

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

