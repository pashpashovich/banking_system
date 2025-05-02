package by.bsuir.bank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import by.bsuir.bank.domain.CurrencyRate;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

class CurrencyConversionServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private CurrencyConversionService currencyConversionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void shouldConvertFromUSDToEUR() {
    // Given
    CurrencyRate usd = new CurrencyRate();
    usd.setCurAbbreviation("USD");
    usd.setCurOfficialRate(BigDecimal.valueOf(3.0));
    usd.setCurScale(1);

    CurrencyRate eur = new CurrencyRate();
    eur.setCurAbbreviation("EUR");
    eur.setCurOfficialRate(BigDecimal.valueOf(3.3));
    eur.setCurScale(1);

    CurrencyRate[] rates = new CurrencyRate[]{usd, eur};
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate[].class))).thenReturn(rates);

    BigDecimal amount = BigDecimal.valueOf(30);

    // When
    BigDecimal result = currencyConversionService.convert(amount, "USD", "EUR");

    // Then
    assertEquals(BigDecimal.valueOf(2730,2), result);
  }

  @Test
  void shouldConvertFromBYNToUSD() {
    // Given
    CurrencyRate usd = new CurrencyRate();
    usd.setCurAbbreviation("USD");
    usd.setCurOfficialRate(BigDecimal.valueOf(3.0));
    usd.setCurScale(1);

    CurrencyRate[] rates = new CurrencyRate[]{usd};
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate[].class))).thenReturn(rates);

    BigDecimal amount = BigDecimal.valueOf(6);

    // When
    BigDecimal result = currencyConversionService.convert(amount, "BYN", "USD");

    // Then
    assertEquals(BigDecimal.valueOf(200,2), result);
  }

  @Test
  void shouldConvertFromEURToBYN() {
    // Given
    CurrencyRate eur = new CurrencyRate();
    eur.setCurAbbreviation("EUR");
    eur.setCurOfficialRate(BigDecimal.valueOf(3.3));
    eur.setCurScale(1);

    CurrencyRate[] rates = new CurrencyRate[]{eur};
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate[].class))).thenReturn(rates);

    BigDecimal amount = BigDecimal.valueOf(10);

    // When
    BigDecimal result = currencyConversionService.convert(amount, "EUR", "BYN");

    // Then
    assertEquals(BigDecimal.valueOf(3300,2), result);
  }

  @Test
  void shouldThrowExceptionWhenFromCurrencyNotFound() {
    // Given
    CurrencyRate[] rates = new CurrencyRate[0];
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate[].class))).thenReturn(rates);

    // When / Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> currencyConversionService.convert(BigDecimal.TEN, "XYZ", "BYN"));

    assertEquals("Currency not found: XYZ", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenToCurrencyNotFound() {
    // Given
    CurrencyRate usd = new CurrencyRate();
    usd.setCurAbbreviation("USD");
    usd.setCurOfficialRate(BigDecimal.valueOf(3.0));
    usd.setCurScale(1);

    CurrencyRate[] rates = new CurrencyRate[]{usd};
    when(restTemplate.getForObject(anyString(), eq(CurrencyRate[].class))).thenReturn(rates);

    // When / Then
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> currencyConversionService.convert(BigDecimal.TEN, "USD", "XXX"));

    assertEquals("Currency not found: XXX", exception.getMessage());
  }
}

