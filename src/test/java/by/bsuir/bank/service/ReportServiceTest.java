package by.bsuir.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.AccountDto;
import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.enumeration.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReportServiceTest {

  @Mock
  private AccountService accountService;

  @Mock
  private TransactionService transactionService;

  @Mock
  private CurrencyConversionService currencyConversionService;

  @InjectMocks
  private ReportService reportService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldGenerateMonthlyTransactionReport() throws Exception {
    // Given
    String accountNum = "123456";
    int month = 5;

    AccountDto accountDto = new AccountDto();
    accountDto.setAccountNum(123456L);
    when(accountService.getAccountById(123456L)).thenReturn(accountDto);

    TransactionDTO transaction = new TransactionDTO();
    transaction.setId(1L);
    transaction.setAmount(100.0);
    transaction.setCurrency("USD");
    transaction.setTransactionTime(LocalDateTime.now());
    transaction.setTransactionType(TransactionType.DEPOSIT);
    transaction.setSenderAccountId(111L);
    transaction.setRecipientAccountId(123456L);

    when(transactionService.findTransactionsByDateRangeAndAccount(
        any(), any(), eq(accountNum))
    ).thenReturn(List.of(transaction));

    when(currencyConversionService.convert(
        BigDecimal.valueOf(100.0), "USD", "BYN")
    ).thenReturn(BigDecimal.valueOf(300.0));

    // When
    byte[] pdfBytes = reportService.generateMonthlyTransactionReport(accountNum, month);

    // Then
    assertThat(pdfBytes).isNotNull();
    assertThat(pdfBytes.length).isGreaterThan(100);
    assertThat(new String(pdfBytes)).contains("%PDF");
  }

  @Test
  void shouldThrowExceptionWhenAccountNotFound() {
    // Given
    when(accountService.getAccountById(999L)).thenReturn(null);

    // When / Then
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> reportService.generateMonthlyTransactionReport("999", 4));
    assertThat(exception.getMessage()).isEqualTo("Account not found");
  }
}
