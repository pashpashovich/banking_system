package ru.clevertec.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.AccountDto;
import ru.clevertec.bank.domain.CheckingAccountDto;
import ru.clevertec.bank.domain.ClientIncomeAccountDto;
import ru.clevertec.bank.domain.ClientIncomeTotalBalanceDto;
import ru.clevertec.bank.domain.CreditAccountDto;
import ru.clevertec.bank.domain.SavingsAccountDto;
import ru.clevertec.bank.domain.SocialAccountDto;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.CheckingAccount;
import ru.clevertec.bank.entity.Client;
import ru.clevertec.bank.entity.CreditAccount;
import ru.clevertec.bank.entity.SavingsAccount;
import ru.clevertec.bank.entity.SocialAccount;
import ru.clevertec.bank.mapper.AccountMapperByInstanceChecks;
import ru.clevertec.bank.repository.AccountRepository;
import ru.clevertec.bank.repository.ClientRepository;
import ru.clevertec.bank.domain.AccountCreationRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ClientRepository clientRepository;

    private final AccountMapperByInstanceChecks accountMapper;

    @Autowired
    public AccountService(AccountRepository accountRepository, AccountMapperByInstanceChecks accountMapper, ClientRepository clientRepository) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.clientRepository = clientRepository;
    }

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
        } else return Optional.empty();
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
