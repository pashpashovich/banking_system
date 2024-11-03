package ru.clevertec.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.bank.domain.AccountDto;
import ru.clevertec.bank.domain.TransactionDTO;
import ru.clevertec.bank.request.AccountCreationRequest;
import ru.clevertec.bank.service.AccountService;
import ru.clevertec.bank.service.CurrencyConversionService;
import ru.clevertec.bank.service.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;


    private final CurrencyConversionService currencyConversionService;

    public AccountController(AccountService accountService, TransactionService transactionService, CurrencyConversionService currencyConversionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.currencyConversionService = currencyConversionService;
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long accountId) {
        AccountDto accountDto = accountService.getAccountById(accountId);
        return ResponseEntity.ok(accountDto);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@PathVariable Long accountId) {
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<AccountDto>> getAccountsByUserId(@PathVariable Long userId) {
        Optional<List<AccountDto>> accounts = accountService.getAccountsByUserId(userId);
        if (accounts.isPresent()) return ResponseEntity.ok(accounts.get());
        else return (ResponseEntity<List<AccountDto>>) ResponseEntity.noContent();
    }

    @GetMapping("/convert/{balance}/{fromCurrency}/{toCurrency}")
    public ResponseEntity<?> convertCurrency(
            @PathVariable String balance,
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency) {
        try {
            BigDecimal convertedBalance = currencyConversionService.convert(
                    new BigDecimal(balance), fromCurrency, toCurrency);
            return ResponseEntity.ok(Collections.singletonMap(toCurrency, convertedBalance.setScale(2, RoundingMode.HALF_UP)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Произошла ошибка: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountCreationRequest request) {
        AccountDto createdAccount = accountService.createAccount(request);
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }


    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountDto> deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        AccountDto accountDto = accountService.deposit(accountId, amount);
        return ResponseEntity.ok(accountDto);
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<AccountDto> withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        try {
            AccountDto accountDto = accountService.withdraw(accountId, amount);
            return ResponseEntity.ok(accountDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
