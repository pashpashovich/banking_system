package by.bsuir.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.AccountCreationRequest;
import by.bsuir.bank.domain.AccountDto;
import by.bsuir.bank.domain.CheckingAccountDto;
import by.bsuir.bank.domain.ClientIncomeAccountDto;
import by.bsuir.bank.domain.ClientIncomeTotalBalanceDto;
import by.bsuir.bank.entity.Account;
import by.bsuir.bank.entity.CheckingAccount;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.entity.enumeration.AccountType;
import by.bsuir.bank.mapper.AccountMapperByInstanceChecks;
import by.bsuir.bank.repository.AccountRepository;
import by.bsuir.bank.repository.ClientRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class AccountServiceTest {

  @Mock
  private AccountRepository accountRepository;
  @Mock
  private ClientRepository clientRepository;
  @Mock
  private AccountMapperByInstanceChecks accountMapper;

  @InjectMocks
  private AccountService accountService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testGetAccountById() {
    Account account = new CheckingAccount();
    account.setAccountNum(1L);
    account.setAccountBalance(BigDecimal.valueOf(100));
    AccountDto dto = new AccountDto();
    dto.setAccountNum(1L);

    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountMapper.mapToAccountDTO(account)).thenReturn(dto);

    AccountDto result = accountService.getAccountById(1L);

    assertEquals(1L, result.getAccountNum());
  }

  @Test
  void testDeposit() {
    Account account = new CheckingAccount();
    account.setAccountBalance(BigDecimal.valueOf(100));
    account.setAccountNum(1L);
    AccountDto dto = new AccountDto();
    dto.setAccountNum(1L);
    dto.setAccountBalance(150.0);

    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountMapper.mapToAccountDTO(account)).thenReturn(dto);

    AccountDto result = accountService.deposit(1L, BigDecimal.valueOf(50));

    assertEquals(150.0, result.getAccountBalance());
    verify(accountRepository).save(account);
  }

  @Test
  void testWithdraw() {
    Account account = new CheckingAccount();
    account.setAccountBalance(BigDecimal.valueOf(100));
    account.setAccountNum(1L);
    AccountDto dto = new AccountDto();
    dto.setAccountNum(1L);
    dto.setAccountBalance(70.0);

    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountMapper.mapToAccountDTO(account)).thenReturn(dto);

    AccountDto result = accountService.withdraw(1L, BigDecimal.valueOf(30));

    assertEquals(70.0, result.getAccountBalance());
    verify(accountRepository).save(account);
  }

  @Test
  void testCreateCheckingAccount() {
    Client client = new Client();
    client.setId(1L);

    CheckingAccountDto dto = new CheckingAccountDto();
    dto.setClientId(1L);
    dto.setAccountNum(123L);
    dto.setAccountBalance(100.0);
    dto.setCurrency("USD");
    dto.setOpenDate(LocalDate.now());
    dto.setAccountActivity(true);
    dto.setOverdraftLimit(BigDecimal.valueOf(200));

    AccountCreationRequest request = new AccountCreationRequest();
    request.setAccountType(AccountType.CHECKING);
    request.setCheckingAccountDto(dto);

    AccountDto resultDto = new AccountDto();
    resultDto.setAccountNum(123L);

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(accountMapper.mapToAccountDTO(any())).thenReturn(resultDto);

    AccountDto result = accountService.createAccount(request);

    assertEquals(123L, result.getAccountNum());
    verify(accountRepository).save(any(Account.class));
  }

  @Test
  void testGetAccountsByUserId() {
    Client client = new Client();
    client.setId(1L);

    Account account = new CheckingAccount();
    account.setAccountNum(1L);
    AccountDto dto = new AccountDto();
    dto.setAccountNum(1L);

    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
    when(accountRepository.findAccountsByClient(client)).thenReturn(List.of(account));
    when(accountMapper.mapToAccountDTO(account)).thenReturn(dto);

    Optional<List<AccountDto>> result = accountService.getAccountsByUserId(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().get(0).getAccountNum());
  }
  @Test
  void testGetAccounts() {
    Account account = new CheckingAccount();
    AccountDto dto = new AccountDto();
    when(accountRepository.findAll()).thenReturn(List.of(account));
    when(accountMapper.mapToAccountDTOList(List.of(account))).thenReturn(List.of(dto));

    List<AccountDto> result = accountService.getAccounts();
    assertEquals(1, result.size());
  }

  @Test
  void testGetClientsIncomeAndAccountCount() {
    Object[] row = new Object[]{1000.0, 2L};
    when(accountRepository.findClientsIncomeAndAccountCount()).thenReturn(Collections.singletonList(row));

    List<ClientIncomeAccountDto> result = accountService.getClientsIncomeAndAccountCount();
    assertEquals(1, result.size());
    assertEquals(1000.0, result.get(0).getIncome());
    assertEquals(2L, result.get(0).getAccountCount());
  }

  @Test
  void testGetClientsIncomeAndTotalBalance() {
    Client client = new Client();
    client.setId(1L);
    client.setIncome(1200.0);

    Account acc1 = new CheckingAccount();
    acc1.setAccountBalance(new BigDecimal("400"));
    Account acc2 = new CheckingAccount();
    acc2.setAccountBalance(new BigDecimal("300"));

    when(clientRepository.findAll()).thenReturn(List.of(client));
    when(accountRepository.findByClientId(1L)).thenReturn(List.of(acc1, acc2));

    List<ClientIncomeTotalBalanceDto> result = accountService.getClientsIncomeAndTotalBalance();
    assertEquals(1, result.size());
    assertEquals(new BigDecimal("700"), result.get(0).getTotalBalance());
    assertEquals(new BigDecimal("1200.0"), result.get(0).getIncome());
  }
}
