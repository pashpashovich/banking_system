package by.bsuir.bank.domain;

import by.bsuir.bank.entity.enumeration.AccountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Data;
import lombok.Getter;

@Data
public class AccountDto {

  private Long accountNum;
  private double accountBalance;
  private String currency;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate openDate;
  private Boolean accountActivity;
  private Long clientId;
  @Getter
  private AccountType accountType;
}
