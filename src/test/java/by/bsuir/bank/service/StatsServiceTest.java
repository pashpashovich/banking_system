package by.bsuir.bank.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.AccountTransactionStatsDTO;
import by.bsuir.bank.domain.DailyTransactionStats;
import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.enumeration.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StatsServiceTest {

  @Mock
  private TransactionService transactionService;

  @Mock
  private CurrencyConversionService currencyConversionService;

  @InjectMocks
  private StatsService statsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldReturnDailyTransactionStats() {
    // Given
    String accountNum = "123";
    int month = LocalDate.now().getMonthValue();

    LocalDateTime transactionDate = LocalDate.now().withMonth(month).withDayOfMonth(10).atTime(12, 0);
    TransactionDTO deposit = new TransactionDTO();
    deposit.setTransactionType(TransactionType.DEPOSIT);
    deposit.setAmount(100.0);
    deposit.setCurrency("USD");
    deposit.setTransactionTime(transactionDate);
    deposit.setRecipientAccountId(Long.valueOf(accountNum));

    when(transactionService.findTransactionsByDateRangeAndAccount(any(), any(), eq(accountNum)))
        .thenReturn(List.of(deposit));
    when(currencyConversionService.convert(BigDecimal.valueOf(100.0), "USD", "BYN"))
        .thenReturn(BigDecimal.valueOf(300.0));

    // When
    Map<Integer, DailyTransactionStats> stats = statsService.getAccountDailyTransactionStats(month, accountNum);

    // Then
    assertThat(stats).containsKey(10);
    DailyTransactionStats dayStats = stats.get(10);
    assertThat(dayStats.getDeposits()).isEqualByComparingTo("300.0");
    assertThat(dayStats.getWithdrawals()).isEqualByComparingTo(BigDecimal.ZERO);
  }


  @Test
  void shouldReturnAccountTransactionStats() {
    // Given
    String accountNum = "123";
    int month = LocalDate.now().getMonthValue();

    // Основные транзакции (для max/min/avg)
    TransactionDTO t1 = new TransactionDTO();
    t1.setAmount(100.0);
    t1.setCurrency("USD");

    TransactionDTO t2 = new TransactionDTO();
    t2.setAmount(200.0);
    t2.setCurrency("USD");

    TransactionDTO deposit = new TransactionDTO();
    deposit.setAmount(100.0);
    deposit.setCurrency("USD");

    TransactionDTO withdrawal = new TransactionDTO();
    withdrawal.setAmount(200.0);
    withdrawal.setCurrency("USD");

    when(transactionService.findTransactionsByDateRangeAndAccount(any(), any(), eq(accountNum)))
        .thenReturn(List.of(t1, t2));

    when(transactionService.findDepositTransactions(eq(accountNum), any(), any()))
        .thenReturn(List.of(deposit));

    when(transactionService.findWithdrawalTransactions(eq(accountNum), any(), any()))
        .thenReturn(List.of(withdrawal));

    when(currencyConversionService.convert(BigDecimal.valueOf(100.0), "USD", "BYN"))
        .thenReturn(BigDecimal.valueOf(300));
    when(currencyConversionService.convert(BigDecimal.valueOf(200.0), "USD", "BYN"))
        .thenReturn(BigDecimal.valueOf(600));

    // When
    AccountTransactionStatsDTO stats = statsService.getAccountTransactionStats(accountNum, month);

    // Then
    assertThat(stats.getMaxTransaction()).isEqualByComparingTo("600");
    assertThat(stats.getMinTransaction()).isEqualByComparingTo("300");
    assertThat(stats.getAvgTransaction()).isEqualByComparingTo("450.00");
    assertThat(stats.getTotalDeposits()).isEqualByComparingTo("300");
    assertThat(stats.getTotalWithdrawals()).isEqualByComparingTo("600");
  }


  @Test
  void shouldReturnTransactionStatsByDateRange() {
    // Given
    LocalDate start = LocalDate.now().minusDays(5);
    LocalDate end = LocalDate.now();

    TransactionDTO t1 = new TransactionDTO();
    t1.setAmount(100.0);
    t1.setCurrency("EUR");

    TransactionDTO t2 = new TransactionDTO();
    t2.setAmount(50.0);
    t2.setCurrency("EUR");

    when(transactionService.findTransactionsByDateRange(any(), any()))
        .thenReturn(List.of(t1, t2));

    when(currencyConversionService.convert(BigDecimal.valueOf(100.0), "EUR", "BYN"))
        .thenReturn(BigDecimal.valueOf(400));
    when(currencyConversionService.convert(BigDecimal.valueOf(50.0), "EUR", "BYN"))
        .thenReturn(BigDecimal.valueOf(200));

    // When
    AccountTransactionStatsDTO stats = statsService.getTransactionStats(start, end);

    // Then
    assertThat(stats.getMaxTransaction()).isEqualByComparingTo("400");
    assertThat(stats.getMinTransaction()).isEqualByComparingTo("200");
    assertThat(stats.getAvgTransaction()).isEqualByComparingTo("300.00");
    assertThat(stats.getTotalDeposits()).isNull();
    assertThat(stats.getTotalWithdrawals()).isNull();
  }
}
