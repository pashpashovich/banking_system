package by.bsuir.bank.service;

import by.bsuir.bank.domain.LoanRequestCreateDTO;
import by.bsuir.bank.domain.LoanRequestDTO;
import by.bsuir.bank.entity.Client;
import by.bsuir.bank.entity.CreditAccount;
import by.bsuir.bank.entity.LoanRequest;
import by.bsuir.bank.entity.enumeration.AccountType;
import by.bsuir.bank.entity.enumeration.LoanStatus;
import by.bsuir.bank.mapper.LoanRequestMapper;
import by.bsuir.bank.repository.AccountRepository;
import by.bsuir.bank.repository.ClientRepository;
import by.bsuir.bank.repository.LoanRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanRequestService {

  private final LoanRequestRepository loanRequestRepository;
  private final AccountRepository accountRepository;
  private final ClientRepository clientRepository;
  private final LoanRequestMapper loanRequestMapper;

  public LoanRequestDTO create(Long clientId, LoanRequestCreateDTO loanRequestCreateDTO) {
    LoanRequest request = new LoanRequest();
    Client client = clientRepository.findById(clientId)
        .orElseThrow(() -> new EntityNotFoundException("Не найден("));
    request.setAmount(loanRequestCreateDTO.getAmount());
    request.setTermInMonths(loanRequestCreateDTO.getTermInMonths());
    request.setPurpose(loanRequestCreateDTO.getPurpose());
    request.setClient(client);
    request.setRequestDate(LocalDate.now());
    request.setStatus(LoanStatus.PENDING);
    request.setReason(evaluate(client, request));
    LoanRequest loanRequest = loanRequestRepository.save(request);
    return loanRequestMapper.toDto(loanRequest);
  }

  public List<LoanRequestDTO> getClientRequests(Long clientId) {
    List<LoanRequest> requests = loanRequestRepository.findByClientId(clientId);
    return loanRequestMapper.toDtoList(requests);
  }

  public List<LoanRequestDTO> getAll() {
    List<LoanRequest> requests = loanRequestRepository.findAll();
    List<LoanRequest> requestList = requests.stream()
        .filter(request -> request.getStatus().equals(LoanStatus.PENDING))
        .collect(Collectors.toList());
    return loanRequestMapper.toDtoList(requestList);
  }

  public LoanRequestDTO updateStatus(Long requestId, LoanStatus status) {
    LoanRequest request = loanRequestRepository.findById(requestId).orElseThrow();
    request.setStatus(status);

    if (status == LoanStatus.APPROVED) {
      CreditAccount creditAccount = new CreditAccount();
      creditAccount.setClient(request.getClient());
      creditAccount.setAccountType(AccountType.CREDIT);
      creditAccount.setAccountBalance(request.getAmount());
      creditAccount.setCurrency("BYN");
      creditAccount.setOpenDate(LocalDate.now());
      creditAccount.setCreditLimit(request.getAmount());
      creditAccount.setAccountActivity(true);
      accountRepository.save(creditAccount);
    } else {
      if (request.getReason().equals("Ожидает решения аналитика.")) {
        request.setReason("Вам отказано по решению сотрудника банка");
      }
    }

    LoanRequest loanRequest = loanRequestRepository.save(request);
    return loanRequestMapper.toDto(loanRequest);
  }

  private String evaluate(Client client, LoanRequest request) {
    if (request.getAmount().compareTo(BigDecimal.valueOf(client.getIncome() * 5)) > 0) {
      return "Сумма займа превышает 5-кратный доход.";
    }
    if (loanRequestRepository.existsByClientIdAndStatus(client.getId(), LoanStatus.APPROVED)) {
      return "У клиента уже есть одобренный займ.";
    }
    return "Ожидает решения аналитика.";
  }
}
