package by.bsuir.bank.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {
  private Long id;
  private BigDecimal amount;
  private int termInMonths;
  private Long clientId;
  private String purpose;
  private String status;
  private String reason;
  private LocalDate requestDate;
}

