package by.bsuir.bank.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CurrencyRate {

  @JsonProperty("Cur_Abbreviation")
  private String curAbbreviation;
  @JsonProperty("Cur_OfficialRate")
  private BigDecimal curOfficialRate;
  @JsonProperty("Cur_Scale")
  private Integer curScale;
}
