package by.bsuir.bank.service;

import by.bsuir.bank.domain.AccountCreationRequest;
import by.bsuir.bank.domain.AccountDto;
import by.bsuir.bank.domain.CheckingAccountDto;
import by.bsuir.bank.domain.ClientIncomeAccountDto;
import by.bsuir.bank.domain.ClientIncomeTotalBalanceDto;
import by.bsuir.bank.domain.CreditAccountDto;
import by.bsuir.bank.domain.SavingsAccountDto;
import by.bsuir.bank.domain.SocialAccountDto;
import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.CheckingAccount;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.entity.CreditAccount;
import by.bsuir.bank.entity.SavingsAccount;
import by.bsuir.bank.entity.SocialAccount;
import by.bsuir.bank.mapper.AccountMapperByInstanceChecks;
import by.bsuir.bank.repository.AccountRepository;
import by.bsuir.bank.repository.ClientRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final ClientRepository clientRepository;
  private final AccountMapperByInstanceChecks accountMapper;

  public AccountDto getAccountById(Long accountId) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    return accountMapper.mapToAccountDTO(account);
  }

  public List<ClientIncomeTotalBalanceDto> getClientsIncomeAndTotalBalance() {
    List<Client> clients = clientRepository.findAll();

    return clients.stream().map(client -> {
      ClientIncomeTotalBalanceDto dto = new ClientIncomeTotalBalanceDto();
      dto.setClientId(client.getId());
      dto.setIncome(BigDecimal.valueOf(client.getIncome()));
      BigDecimal totalBalance = accountRepository.findByClientId(client.getId())
          .stream()
          .map(Account::getAccountBalance)
          .reduce(BigDecimal.ZERO, BigDecimal::add);
      dto.setTotalBalance(totalBalance);
      return dto;
    }).collect(Collectors.toList());
  }


  public AccountDto createAccount(AccountCreationRequest accountCreationRequest) {
    Account account;

    Client client = clientRepository.findById(
        switch (accountCreationRequest.getAccountType()) {
          case CHECKING -> accountCreationRequest.getCheckingAccountDto().getClientId();
          case SAVINGS -> accountCreationRequest.getSavingsAccountDto().getClientId();
          case SOCIAL -> accountCreationRequest.getSocialAccountDto().getClientId();
          case CREDIT -> accountCreationRequest.getCreditAccountDto().getClientId();
        }
    ).orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));
    switch (accountCreationRequest.getAccountType()) {
      case CHECKING -> {
        CheckingAccountDto dto = accountCreationRequest.getCheckingAccountDto();
        account = new CheckingAccount(
            dto.getAccountNum(),
            client,
            BigDecimal.valueOf(dto.getAccountBalance()),
            dto.getCurrency(),
            dto.getOpenDate(),
            dto.getAccountActivity(),
            dto.getOverdraftLimit()
        );
      }
      case SAVINGS -> {
        SavingsAccountDto dto = accountCreationRequest.getSavingsAccountDto();
        account = new SavingsAccount(
            dto.getAccountNum(),
            client,
            BigDecimal.valueOf(dto.getAccountBalance()),
            dto.getCurrency(),
            dto.getOpenDate(),
            dto.getAccountActivity(),
            dto.getInterestRate()
        );
      }
      case SOCIAL -> {
        SocialAccountDto dto = accountCreationRequest.getSocialAccountDto();
        account = new SocialAccount(
            dto.getAccountNum(),
            client,
            BigDecimal.valueOf(dto.getAccountBalance()),
            dto.getCurrency(),
            dto.getOpenDate(),
            dto.getAccountActivity(),
            dto.getSocialPayments()
        );
      }
      case CREDIT -> {
        CreditAccountDto dto = accountCreationRequest.getCreditAccountDto();
        account = new CreditAccount(
            dto.getAccountNum(),
            client,
            BigDecimal.valueOf(dto.getAccountBalance()),
            dto.getCurrency(),
            dto.getOpenDate(),
            dto.getAccountActivity(),
            dto.getCreditLimit()
        );
      }
      default -> throw new IllegalArgumentException("Невалидный тип счета");
    }

    accountRepository.save(account);
    return accountMapper.mapToAccountDTO(account);
  }

  public AccountDto deposit(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Account not found"));

    account.deposit(amount);
    accountRepository.save(account);

    return accountMapper.mapToAccountDTO(account);
  }

  public AccountDto withdraw(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId)
        .orElseThrow(() -> new IllegalArgumentException("Счет не найден"));

    account.withdraw(amount);
    accountRepository.save(account);

    return accountMapper.mapToAccountDTO(account);
  }

  public Optional<List<AccountDto>> getAccountsByUserId(Long userId) {
    Optional<Client> client = clientRepository.findById(userId);
    List<Account> accounts;
    if (client.isPresent()) {
      accounts = accountRepository.findAccountsByClient(client.get());
      return Optional.of(accounts.stream()
          .map(accountMapper::mapToAccountDTO)
          .collect(Collectors.toList()));
    } else {
      return Optional.empty();
    }
  }

  public List<AccountDto> getAccounts() {
    List<Account> accounts = accountRepository.findAll();
    return accountMapper.mapToAccountDTOList(accounts);
  }

  public List<ClientIncomeAccountDto> getClientsIncomeAndAccountCount() {
    return accountRepository.findClientsIncomeAndAccountCount().stream()
        .map(result -> new ClientIncomeAccountDto((Double) result[0], (Long) result[1]))
        .toList();
  }
}
