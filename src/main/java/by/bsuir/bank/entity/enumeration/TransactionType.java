package by.bsuir.bank.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType {
    TRANSFER("Перевод"),
    DEPOSIT("Пополнение"),
    WITHDRAWAL("Снятие");

    private final String displayName;
}
