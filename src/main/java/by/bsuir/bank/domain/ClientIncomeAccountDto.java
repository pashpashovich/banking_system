package by.bsuir.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientIncomeAccountDto {

  private double income;
  private long accountCount;
}
