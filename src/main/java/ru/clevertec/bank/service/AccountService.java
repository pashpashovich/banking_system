package ru.clevertec.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.clevertec.bank.domain.AccountDto;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.CheckingAccount;
import ru.clevertec.bank.entity.Client;
import ru.clevertec.bank.entity.CreditAccount;
import ru.clevertec.bank.entity.SavingsAccount;
import ru.clevertec.bank.entity.SocialAccount;
import ru.clevertec.bank.mapper.AccountMapperByInstanceChecks;
import ru.clevertec.bank.repository.AccountRepository;
import ru.clevertec.bank.repository.ClientRepository;
import ru.clevertec.bank.request.AccountCreationRequest;

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


    public AccountDto createAccount(AccountCreationRequest accountCreationRequest) {
        Account account;
        switch (accountCreationRequest.getAccountType()) {
            case CHECKING -> {
                account = new CheckingAccount(
                        accountCreationRequest.getCheckingAccountDto().getAccountNum(),
                        clientRepository.findById(accountCreationRequest.getCheckingAccountDto().getClientId()).get(),
                        BigDecimal.valueOf(accountCreationRequest.getCheckingAccountDto().getAccountBalance()),
                        accountCreationRequest.getCheckingAccountDto().getCurrency(),
                        accountCreationRequest.getCheckingAccountDto().getOpenDate(),
                        accountCreationRequest.getCheckingAccountDto().getAccountActivity(),
                        accountCreationRequest.getCheckingAccountDto().getOverdraftLimit()
                );
            }
            case SAVINGS -> {
                account = new SavingsAccount(
                        accountCreationRequest.getSavingsAccountDto().getAccountNum(),
                        clientRepository.findById(accountCreationRequest.getSavingsAccountDto().getClientId()).get(),
                        BigDecimal.valueOf(accountCreationRequest.getSavingsAccountDto().getAccountBalance()),
                        accountCreationRequest.getSavingsAccountDto().getCurrency(),
                        accountCreationRequest.getSavingsAccountDto().getOpenDate(),
                        accountCreationRequest.getSavingsAccountDto().getAccountActivity(),
                        accountCreationRequest.getSavingsAccountDto().getInterestRate()
                );
            }
            case SOCIAL -> {
                account = new SocialAccount(
                        accountCreationRequest.getSocialAccountDto().getAccountNum(),
                        clientRepository.findById(accountCreationRequest.getSocialAccountDto().getClientId()).get(),
                        BigDecimal.valueOf(accountCreationRequest.getSocialAccountDto().getAccountBalance()),
                        accountCreationRequest.getSocialAccountDto().getCurrency(),
                        accountCreationRequest.getSocialAccountDto().getOpenDate(),
                        accountCreationRequest.getSocialAccountDto().getAccountActivity(),
                        accountCreationRequest.getSocialAccountDto().getSocialPayments()
                );
            }
            case CREDIT -> {
                account = new CreditAccount(
                        accountCreationRequest.getCreditAccountDto().getAccountNum(),
                        clientRepository.findById(accountCreationRequest.getCreditAccountDto().getClientId()).get(),
                        BigDecimal.valueOf(accountCreationRequest.getCreditAccountDto().getAccountBalance()),
                        accountCreationRequest.getCreditAccountDto().getCurrency(),
                        accountCreationRequest.getCreditAccountDto().getOpenDate(),
                        accountCreationRequest.getCreditAccountDto().getAccountActivity(),
                        accountCreationRequest.getCreditAccountDto().getCreditLimit()
                );
            }
            default -> throw new IllegalArgumentException("Invalid account type");
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
}
