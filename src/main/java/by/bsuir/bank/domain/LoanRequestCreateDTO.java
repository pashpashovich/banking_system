package by.bsuir.bank.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestCreateDTO {
  private BigDecimal amount;
  private int termInMonths;
  private String purpose;
}
