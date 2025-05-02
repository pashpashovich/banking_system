package by.bsuir.bank.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SocialAccountDto extends AccountDto {

  private Boolean socialPayments;
}
