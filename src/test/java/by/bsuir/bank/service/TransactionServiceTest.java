package by.bsuir.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.CheckingAccount;
import by.bsuir.bank.entity.Transaction;
import by.bsuir.bank.entity.enumeration.TransactionType;
import by.bsuir.bank.mapper.TransactionMapper;
import by.bsuir.bank.repository.AccountRepository;
import by.bsuir.bank.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

  @Mock
  private TransactionRepository transactionRepository;
  @Mock
  private AccountRepository accountRepository;
  @Mock
  private CurrencyConversionService currencyConversionService;
  @Mock
  private TransactionMapper transactionMapper;

  @InjectMocks
  private TransactionService transactionService;

  private final Long senderId = 1L;
  private final Long recipientId = 2L;

  private Transaction transaction;
  private TransactionDTO dto;
  private Account sender;
  private Account recipient;

  @BeforeEach
  void init() {
    sender = new CheckingAccount();
    sender.setAccountNum(senderId);
    sender.setCurrency("USD");
    sender.setAccountBalance(BigDecimal.valueOf(1000));

    recipient = new CheckingAccount();
    recipient.setAccountNum(recipientId);
    recipient.setCurrency("EUR");
    recipient.setAccountBalance(BigDecimal.valueOf(500));

    transaction = new Transaction();
    transaction.setId(10L);
    transaction.setAmount(BigDecimal.valueOf(100));
    transaction.setCurrency("USD");
    transaction.setTransactionTime(LocalDateTime.now());
    transaction.setTransactionType(TransactionType.TRANSFER);

    dto = new TransactionDTO();
    dto.setId(10L);
    dto.setAmount(100);
    dto.setCurrency("USD");
    dto.setTransactionTime(transaction.getTransactionTime());
    dto.setTransactionType(TransactionType.TRANSFER);
    dto.setSenderAccountId(senderId);
    dto.setRecipientAccountId(recipientId);
  }

  @Test
  void shouldCreateTransactionAndReturnDto() {
    // given
    when(accountRepository.findAccountByAccountNum(senderId)).thenReturn(sender);
    when(accountRepository.findAccountByAccountNum(recipientId)).thenReturn(recipient);
    when(currencyConversionService.convert(any(), eq("USD"), eq("USD"))).thenReturn(BigDecimal.valueOf(100));
    when(currencyConversionService.convert(any(), eq("USD"), eq("EUR"))).thenReturn(BigDecimal.valueOf(90));
    when(transactionMapper.toDto(transaction)).thenReturn(dto);

    // when
    TransactionDTO result = transactionService.createTransaction(transaction, senderId, recipientId);

    // then
    verify(transactionRepository).save(transaction);
    assertThat(result).isNotNull();
    assertThat(result.getAmount()).isEqualTo(100.0);
    assertThat(result.getSenderAccountId()).isEqualTo(senderId);
    assertThat(result.getRecipientAccountId()).isEqualTo(recipientId);
  }

  @Test
  void shouldFindTransactionById() {
    // given
    transaction.setSenderAccount(sender);
    transaction.setRecipientAccount(recipient);
    when(transactionRepository.findById(10L)).thenReturn(Optional.of(transaction));
    when(transactionMapper.toDto(transaction)).thenReturn(dto);

    // when
    TransactionDTO result = transactionService.findTransactionById(10L);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(10L);
    assertThat(result.getSenderAccountId()).isEqualTo(senderId);
    assertThat(result.getRecipientAccountId()).isEqualTo(recipientId);
  }

  @Test
  void shouldReturnTransactionsByAccount() {
    // given
    when(accountRepository.findAccountByAccountNum(senderId)).thenReturn(sender);
    transaction.setSenderAccount(sender);
    transaction.setRecipientAccount(recipient);
    when(transactionRepository.findBySenderAccountOrRecipientAccount(sender, sender)).thenReturn(List.of(transaction));
    when(transactionMapper.toDto(transaction)).thenReturn(dto);

    // when
    List<TransactionDTO> result = transactionService.getTransactionsByAccount(senderId);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSenderAccountId()).isEqualTo(senderId);
  }

  @Test
  void shouldReturnTransactionsByDateRange() {
    // given
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now();
    transaction.setSenderAccount(sender);
    transaction.setRecipientAccount(recipient);

    when(transactionRepository.findByTransactionTimeBetween(start, end)).thenReturn(List.of(transaction));
    when(transactionMapper.toDto(transaction)).thenReturn(dto);

    // when
    List<TransactionDTO> result = transactionService.findTransactionsByDateRange(start, end);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(10L);
  }
}
