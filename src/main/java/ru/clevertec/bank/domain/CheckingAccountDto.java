package ru.clevertec.bank.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckingAccountDto extends AccountDto {
    private BigDecimal overdraftLimit;
}
