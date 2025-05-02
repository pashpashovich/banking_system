package by.bsuir.bank.domain;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ClientIncomeTotalBalanceDto {

  private Long clientId;
  private BigDecimal income;
  private BigDecimal totalBalance;
}
