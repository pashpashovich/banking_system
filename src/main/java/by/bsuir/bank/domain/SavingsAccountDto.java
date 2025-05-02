package by.bsuir.bank.domain;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SavingsAccountDto extends AccountDto {

  private BigDecimal interestRate;
}
