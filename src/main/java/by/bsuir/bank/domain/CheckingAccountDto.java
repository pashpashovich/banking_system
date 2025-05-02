package by.bsuir.bank.domain;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckingAccountDto extends AccountDto {

  private BigDecimal overdraftLimit;
}
