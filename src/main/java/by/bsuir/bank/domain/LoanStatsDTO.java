package by.bsuir.bank.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoanStatsDTO {
  private int requestCount;
  private BigDecimal totalAmount;
  private long approvedCount;
}
