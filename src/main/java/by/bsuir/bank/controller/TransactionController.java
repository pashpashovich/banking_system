package by.bsuir.bank.controller;

import by.bsuir.bank.domain.AccountTransactionStatsDTO;
import by.bsuir.bank.domain.DailyTransactionStats;
import by.bsuir.bank.domain.MaxTransactionStatsDto;
import by.bsuir.bank.domain.TransactionDTO;
import by.bsuir.bank.entity.Transaction;
import by.bsuir.bank.entity.enumeration.TransactionType;
import by.bsuir.bank.mapper.TransactionMapper;
import by.bsuir.bank.service.PdfReceiptService;
import by.bsuir.bank.service.ReportService;
import by.bsuir.bank.service.StatsService;
import by.bsuir.bank.service.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

  private final TransactionService transactionService;
  private final TransactionMapper transactionMapper;
  private final PdfReceiptService pdfReceiptService;
  private final StatsService statsService;
  private final ReportService reportService;

  @GetMapping("/")
  public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
    List<TransactionDTO> transactionList = transactionService.getTransactions();
    return ResponseEntity.ok(transactionList);
  }

  @GetMapping("/by-date")
  public ResponseEntity<List<TransactionDTO>> getAllTransactionsByDate(
      @RequestParam("startDate") LocalDate startDate1,
      @RequestParam("endDate") LocalDate endDate1) {
    LocalDateTime startDate = startDate1.atStartOfDay();
    LocalDateTime endDate = endDate1.atTime(23, 59, 59);
    List<TransactionDTO> transactionList = transactionService.findTransactionsByDateRange(startDate, endDate);
    return ResponseEntity.ok(transactionList);
  }

  @PostMapping("/")
  public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
    Transaction transaction = transactionMapper.toEntity(transactionDTO);
    TransactionDTO savedTransaction = transactionService.createTransaction(transaction,
        transactionDTO.getSenderAccountId(), transactionDTO.getRecipientAccountId());
    return ResponseEntity.ok(savedTransaction);
  }

  @GetMapping("/receipt/{transactionId}")
  public ResponseEntity<byte[]> generateReceipt(@PathVariable Long transactionId) {
    try {
      TransactionDTO transaction = transactionService.findTransactionById(transactionId);
      if (transaction == null) {
        return ResponseEntity.notFound().build();
      }
      byte[] pdf = pdfReceiptService.generateReceiptPdf(transaction);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", "receipt_" + transactionId + ".pdf");
      return ResponseEntity.ok().headers(headers).body(pdf);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).build();
    }
  }

  @GetMapping("/{accountNum}/{month}/daily")
  public ResponseEntity<List<DailyTransactionStats>> getAccountDailyTransactionStats(
      @PathVariable String accountNum,
      @PathVariable int month) {
    Map<Integer, DailyTransactionStats> dailyStats = statsService.getAccountDailyTransactionStats(month, accountNum);
    List<DailyTransactionStats> response = new ArrayList<>(dailyStats.values());
    response.sort(Comparator.comparingInt(DailyTransactionStats::getDay));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{accountNum}/{month}")
  public ResponseEntity<AccountTransactionStatsDTO> getAccountTransactionStats(
      @PathVariable String accountNum,
      @PathVariable int month) {
    AccountTransactionStatsDTO stats = statsService.getAccountTransactionStats(accountNum, month);
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/stats")
  public ResponseEntity<AccountTransactionStatsDTO> getTransactionStats(
      @RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate) {
    AccountTransactionStatsDTO stats = statsService.getTransactionStats(startDate, endDate);
    return ResponseEntity.ok(stats);
  }

  @GetMapping("/max-stats")
  public ResponseEntity<MaxTransactionStatsDto> getMaxTransactionsByDate(
      @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    MaxTransactionStatsDto maxTransactions = transactionService.getMaxTransactionsByDateRange(startDate, endDate);
    return ResponseEntity.ok(maxTransactions);
  }

  @GetMapping("/report/{accountNum}/{month}")
  public ResponseEntity<byte[]> generateMonthlyTransactionReport(
      @PathVariable String accountNum,
      @PathVariable int month) {
    try {
      byte[] pdfReport = reportService.generateMonthlyTransactionReport(accountNum, month);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", "transaction_report_" + accountNum + "_" + month + ".pdf");

      return ResponseEntity.ok().headers(headers).body(pdfReport);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).build();
    }
  }

  @GetMapping("/count-by-type")
  public ResponseEntity<Map<TransactionType, Long>> getTransactionCountsByType(
      @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    Map<TransactionType, Long> transactionCounts = transactionService.countTransactionsByTypeAndDateRange(startDate,
        endDate);
    return ResponseEntity.ok(transactionCounts);
  }

  @GetMapping("/generate-pdf")
  public ResponseEntity<byte[]> generateTransactionReport(@RequestParam("startDate") LocalDate startDate,
      @RequestParam("endDate") LocalDate endDate,
      @RequestParam("firstName") String firstName,
      @RequestParam("secondName") String secondName,
      @RequestParam("patronymicName") String patronymicName) {
    try {
      byte[] pdfReport = reportService.generateTransactionReport(startDate, endDate, firstName, secondName,
          patronymicName);
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_PDF);
      headers.setContentDispositionFormData("attachment", "transaction_report_" + "_" + startDate + ".pdf");
      return ResponseEntity.ok().headers(headers).body(pdfReport);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).build();
    }
  }

  @GetMapping("/boxplot")
  public ResponseEntity<?> getBoxPlotData(
      @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam("transaction_type") String transactionType) {

    List<Transaction> transactions = transactionService.findTransactionsByDateAndType(startDate, endDate,
        transactionType);

    if (transactions.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    List<BigDecimal> amounts = transactions.stream()
        .map(Transaction::getAmount)
        .toList();
    Map<String, Object> response = new HashMap<>();
    response.put("labels", Collections.singletonList(transactionType));
    response.put("datasets", Collections.singletonList(Collections.singletonMap("data", amounts)));

    return ResponseEntity.ok(response);
  }

}
