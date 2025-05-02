package by.bsuir.bank.mapper;

import by.bsuir.bank.domain.AccountDto;
import by.bsuir.bank.domain.CheckingAccountDto;
import by.bsuir.bank.domain.CreditAccountDto;
import by.bsuir.bank.domain.SavingsAccountDto;
import by.bsuir.bank.domain.SocialAccountDto;
import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.CheckingAccount;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.entity.CreditAccount;
import by.bsuir.bank.entity.SavingsAccount;
import by.bsuir.bank.entity.SocialAccount;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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

  @Mapping(target = "client", ignore = true)
  @Mapping(source = "overdraftLimit", target = "overdraftLimit")
  CheckingAccount map(CheckingAccountDto accountDto);

  @Mapping(target = "client", ignore = true)
  @Mapping(source = "interestRate", target = "interestRate")
  SavingsAccount map(SavingsAccountDto accountDto);

  @Mapping(target = "client", ignore = true)
  @Mapping(source = "creditLimit", target = "creditLimit")
  CreditAccount map(CreditAccountDto accountDto);

  @Mapping(target = "client", ignore = true)
  SocialAccount map(SocialAccountDto accountDto);

  default Long map(Client client) {
    return client != null ? client.getId() : null;
  }

  default List<AccountDto> mapToAccountDTOList(List<Account> accounts) {
    return accounts.stream()
        .map(this::mapToAccountDTO)
        .toList();
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
}

