package by.bsuir.bank.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum LoanStatus {
  PENDING("Ожидает ответа"),
  APPROVED("Одобрен"),
  REJECTED("Отказано");
  private final String displayName;
}
