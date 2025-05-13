package by.bsuir.bank.controller;

import by.bsuir.bank.domain.LoanRequestCreateDTO;
import by.bsuir.bank.domain.LoanRequestDTO;
import by.bsuir.bank.service.LoanRequestService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanClientController {

  private final LoanRequestService loanRequestService;

  @PostMapping
  public ResponseEntity<LoanRequestDTO> create(@RequestParam Long clientId, @RequestBody LoanRequestCreateDTO dto) {
    LoanRequestDTO request = loanRequestService.create(clientId, dto);
    return ResponseEntity.ok(request);
  }

  @GetMapping("/my")
  public ResponseEntity<List<LoanRequestDTO>> myLoans(@RequestParam Long clientId) {
    List<LoanRequestDTO> clientRequests = loanRequestService.getClientRequests(clientId);
    return ResponseEntity.ok(clientRequests);
  }
}
