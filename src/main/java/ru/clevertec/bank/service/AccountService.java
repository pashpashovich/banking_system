package ru.clevertec.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.AccountDto;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Client;
import ru.clevertec.bank.mapper.AccountMapperByInstanceChecks;
import ru.clevertec.bank.repository.AccountRepository;
import ru.clevertec.bank.repository.ClientRepository;

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
        this.clientRepository=clientRepository;
    }

    public AccountDto getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        return accountMapper.mapToAccountDTO(account);
    }

    public AccountDto createAccount(AccountDto accountDto) {
        Account account = accountMapper.mapToAccountEntity(accountDto);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.mapToAccountDTO(savedAccount);
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
        Optional<Client> client=clientRepository.findById(userId);
        List<Account> accounts;
        if(client.isPresent()) {
            accounts = accountRepository.findAccountsByClient(client.get());
            return Optional.of(accounts.stream()
                    .map(accountMapper::mapToAccountDTO)
                    .collect(Collectors.toList()));
        } else return Optional.empty();
    }
}
