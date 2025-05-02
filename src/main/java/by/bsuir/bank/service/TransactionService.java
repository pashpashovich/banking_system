package by.bsuir.bank.service;

import by.bsuir.bank.domain.MaxTransactionStatsDto;
import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.Transaction;
import by.bsuir.bank.entity.enumeration.TransactionType;
import by.bsuir.bank.mapper.TransactionMapper;
import by.bsuir.bank.repository.AccountRepository;
import by.bsuir.bank.repository.TransactionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TransactionService {

  private final TransactionRepository transactionRepository;
  private final AccountRepository accountRepository;
  private final CurrencyConversionService currencyConversionService;
  private final TransactionMapper transactionMapper;

  @Transactional
  public TransactionDTO createTransaction(Transaction transaction, Long senderAccountNum, Long recipientAccountNum) {
    Account senderAccount = accountRepository.findAccountByAccountNum(senderAccountNum);
    Account recipientAccount = accountRepository.findAccountByAccountNum(recipientAccountNum);
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
    if (transaction.get().getSenderAccount() != null) {
      transactionDTO.setSenderAccountId(transaction.get().getSenderAccount().getAccountNum());
    }
    if (transaction.get().getRecipientAccount() != null) {
      transactionDTO.setRecipientAccountId(transaction.get().getRecipientAccount().getAccountNum());
    }
    return transactionDTO;
  }

  public List<TransactionDTO> getTransactionsByAccount(Long accountId) {
    Account account = accountRepository.findAccountByAccountNum(accountId);
    List<Transaction> transactions = transactionRepository.findBySenderAccountOrRecipientAccount(account, account);
    if (transactions.isEmpty()) {
      return Collections.emptyList();
    }
    return transactions.stream()
        .map(transaction -> {
          TransactionDTO dto = transactionMapper.toDto(transaction);
          if (transaction.getSenderAccount() != null) {
            dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
          }
          if (transaction.getRecipientAccount() != null) {
            dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
          }
          return dto;
        })
        .collect(Collectors.toList());
  }

  public List<TransactionDTO> findTransactionsByDateRangeAndAccount(LocalDateTime startDate, LocalDateTime endDate,
      String accountNum) {
    Account account = accountRepository.findAccountByAccountNum(Long.valueOf(accountNum));
    List<Transaction> transactionList = transactionRepository.findTransactionsByTimeRangeAndRecipientOrSender(startDate,
        endDate, account);
    return transactionList.stream()
        .map(transaction -> {
          TransactionDTO dto = transactionMapper.toDto(transaction);
          if (transaction.getSenderAccount() != null) {
            dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
          }
          if (transaction.getRecipientAccount() != null) {
            dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
          }
          return dto;
        })
        .collect(Collectors.toList());
  }

  public List<TransactionDTO> findTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    List<Transaction> transactionList = transactionRepository.findByTransactionTimeBetween(startDate, endDate);
    return transactionList.stream()
        .map(transaction -> {
          TransactionDTO dto = transactionMapper.toDto(transaction);
          if (transaction.getSenderAccount() != null) {
            dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
          }
          if (transaction.getRecipientAccount() != null) {
            dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
          }
          return dto;
        })
        .toList();
  }

  public List<TransactionDTO> findDepositTransactions(String accountNum, LocalDateTime startDate,
      LocalDateTime endDate) {
    Account account = accountRepository.findAccountByAccountNum(Long.valueOf(accountNum));
    List<Transaction> transactionList = transactionRepository.findByRecipientAccountAndTransactionTypeOrTransactionTypeAndTransactionTimeBeforeAndTransactionTimeAfter(
        account, TransactionType.DEPOSIT, TransactionType.TRANSFER, endDate, startDate);
    return transactionList.stream()
        .map(transaction -> {
          TransactionDTO dto = transactionMapper.toDto(transaction);
          if (transaction.getSenderAccount() != null) {
            dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
          }
          if (transaction.getRecipientAccount() != null) {
            dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
          }
          return dto;
        })
        .collect(Collectors.toList());
  }

  public List<TransactionDTO> findWithdrawalTransactions(String accountNum, LocalDateTime startDate,
      LocalDateTime endDate) {
    Account account = accountRepository.findAccountByAccountNum(Long.valueOf(accountNum));
    List<Transaction> transactionList = transactionRepository.findTransactionsBySenderAccountAndTypesWithinDateRange(
        account, TransactionType.WITHDRAWAL, TransactionType.TRANSFER, endDate, startDate);
    return transactionList.stream()
        .map(transaction -> {
          TransactionDTO dto = transactionMapper.toDto(transaction);
          if (transaction.getSenderAccount() != null) {
            dto.setSenderAccountId(transaction.getSenderAccount().getAccountNum());
          }
          if (transaction.getRecipientAccount() != null) {
            dto.setRecipientAccountId(transaction.getRecipientAccount().getAccountNum());
          }
          return dto;
        })
        .collect(Collectors.toList());
  }

  public List<TransactionDTO> getTransactions() {
    List<Transaction> transactions = transactionRepository.findAll();
    return transactionMapper.toDto(transactions);
  }

  public List<Transaction> findTransactionsByDateAndType(LocalDate startDate, LocalDate endDate,
      String transactionType) {
    return transactionRepository.findByTransactionTimeBetweenAndTransactionType(
        startDate.atStartOfDay(),
        endDate.atTime(23, 59, 59),
        TransactionType.valueOf(transactionType.toUpperCase())
    );
  }

  public MaxTransactionStatsDto getMaxTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
    List<Transaction> transactions = transactionRepository.findTransactionsByTransactionTimeBetween(
        startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    Map<LocalDate, Map<TransactionType, Double>> maxTransactionsByDate = transactions.stream()
        .collect(Collectors.groupingBy(
            transaction -> transaction.getTransactionTime().toLocalDate(),
            Collectors.groupingBy(
                Transaction::getTransactionType,
                Collectors.collectingAndThen(
                    Collectors.maxBy(Comparator.comparingDouble(t -> t.getAmount().doubleValue())),
                    optional -> optional.map(transaction -> transaction.getAmount().doubleValue()).orElse(0.0)
                )
            )
        ));

    return new MaxTransactionStatsDto(maxTransactionsByDate);
  }

  public Map<TransactionType, Long> countTransactionsByTypeAndDateRange(LocalDate startDate, LocalDate endDate) {
    LocalDateTime startDateTime = startDate.atStartOfDay();
    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
    return transactionRepository.findTransactionsByTransactionTimeBetween(startDateTime, endDateTime).stream()
        .collect(Collectors.groupingBy(Transaction::getTransactionType, Collectors.counting()));
  }
}
