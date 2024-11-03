package ru.clevertec.bank.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.clevertec.bank.domain.TransactionDTO;
import ru.clevertec.bank.entity.Account;
import ru.clevertec.bank.entity.Transaction;
import ru.clevertec.bank.entity.enumeration.TransactionType;
import ru.clevertec.bank.mapper.TransactionMapper;
import ru.clevertec.bank.repository.AccountRepository;
import ru.clevertec.bank.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CurrencyConversionService currencyConversionService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionDTO createTransaction(Transaction transaction, Long senderAccountNum, Long recipientAccountNum) {
        Account senderAccount=accountRepository.findAccountByAccountNum(senderAccountNum);
        Account recipientAccount=accountRepository.findAccountByAccountNum(recipientAccountNum);
        transaction.setSenderAccount(senderAccount);
        transaction.setRecipientAccount(recipientAccount);
        transaction.updateAccountBalances(currencyConversionService);
        transactionRepository.save(transaction);
        TransactionDTO dto = transactionMapper.toDto(transaction);
        dto.setSenderAccountId(senderAccountNum);
        dto.setRecipientAccountId(recipientAccountNum);
        return dto;
    }

    public TransactionDTO findTransactionById(Long transactionId) {
        Optional<Transaction> transaction = transactionRepository.findById(transactionId);
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction.orElse(null));
        if (transaction.get().getSenderAccount()!=null) transactionDTO.setSenderAccountId(transaction.get().getSenderAccount().getAccountNum());
        if (transaction.get().getRecipientAccount()!=null)transactionDTO.setRecipientAccountId(transaction.get().getRecipientAccount().getAccountNum());
        return transactionDTO;
    }

    public List<TransactionDTO> getTransactionsByAccount(Long accountId) {
        Account account = accountRepository.findAccountByAccountNum(accountId);
        List<Transaction> transactions = transactionRepository.findBySenderAccountOrRecipientAccount(account,account);
        if (transactions.isEmpty()) return Collections.emptyList();
        return transactions.stream()
                .map(transaction -> {
                    TransactionDTO dto = transactionMapper.toDto(transaction);
                       if( transaction.getSenderAccount()!=null)
                    dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
                    if( transaction.getRecipientAccount()!=null)
                        dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findTransactionsByDateRangeAndAccount(LocalDateTime startDate, LocalDateTime endDate, String accountNum) {
        Account account = accountRepository.findAccountByAccountNum(Long.valueOf(accountNum));
        List<Transaction> transactionList = transactionRepository.findTransactionsByTimeRangeAndRecipientOrSender(startDate, endDate, account);
        return transactionList.stream()
                .map(transaction -> {
                    TransactionDTO dto = transactionMapper.toDto(transaction);
                    if( transaction.getSenderAccount()!=null)
                        dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
                    if( transaction.getRecipientAccount()!=null)
                        dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findDepositTransactions(String accountNum, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = accountRepository.findAccountByAccountNum(Long.valueOf(accountNum));
        List<Transaction> transactionList = transactionRepository.findByRecipientAccountAndTransactionTypeOrTransactionTypeAndTransactionTimeBeforeAndTransactionTimeAfter(account, TransactionType.DEPOSIT, TransactionType.TRANSFER, endDate, startDate );
        return transactionList.stream()
                .map(transaction -> {
                    TransactionDTO dto = transactionMapper.toDto(transaction);
                    if( transaction.getSenderAccount()!=null)
                        dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
                    if( transaction.getRecipientAccount()!=null)
                        dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> findWithdrawalTransactions(String accountNum, LocalDateTime startDate, LocalDateTime endDate) {
        Account account = accountRepository.findAccountByAccountNum(Long.valueOf(accountNum));
        List<Transaction> transactionList = transactionRepository.findTransactionsBySenderAccountAndTypesWithinDateRange(account, TransactionType.WITHDRAWAL, TransactionType.TRANSFER, endDate,startDate);
        return transactionList.stream()
                .map(transaction -> {
                    TransactionDTO dto = transactionMapper.toDto(transaction);
                    if( transaction.getSenderAccount()!=null)
                        dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
                    if( transaction.getRecipientAccount()!=null)
                        dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}