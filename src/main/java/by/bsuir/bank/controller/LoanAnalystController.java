package by.bsuir.bank.controller;

import by.bsuir.bank.domain.LoanRequestDTO;
import by.bsuir.bank.domain.LoanStatsDTO;
import by.bsuir.bank.entity.enumeration.LoanStatus;
import by.bsuir.bank.service.LoanRequestService;
import by.bsuir.bank.service.StatsService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loan-management")
@RequiredArgsConstructor
public class LoanAnalystController {

  private final LoanRequestService loanRequestService;
  private final StatsService statsService;

  @GetMapping
  public ResponseEntity<List<LoanRequestDTO>> all() {
    List<LoanRequestDTO> dtoList = loanRequestService.getAll();
    return ResponseEntity.ok(dtoList);
  }

  @GetMapping("/regions")
  public ResponseEntity<Map<String, LoanStatsDTO>> getStats() {
    Map<String, LoanStatsDTO> loanStatsByRegion = statsService.getLoanStatsByRegion();
    return ResponseEntity.ok(loanStatsByRegion);
  }

  @PutMapping("/{id}/status")
  public ResponseEntity<LoanRequestDTO> updateStatus(@PathVariable Long id, @RequestParam LoanStatus status) {
    LoanRequestDTO loanRequestDTO = loanRequestService.updateStatus(id, status);
    return ResponseEntity.ok(loanRequestDTO);
  }
}

