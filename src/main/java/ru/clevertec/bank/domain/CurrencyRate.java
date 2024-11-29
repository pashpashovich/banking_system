package ru.clevertec.bank.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyRate {
    @JsonProperty("Cur_Abbreviation")
    private String curAbbreviation;
    @JsonProperty("Cur_OfficialRate")
    private BigDecimal curOfficialRate;
    @JsonProperty("Cur_Scale")
    private Integer curScale;
}