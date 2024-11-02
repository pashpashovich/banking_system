package ru.clevertec.bank.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreditAccountDto extends AccountDto {
    private BigDecimal creditLimit;
}
