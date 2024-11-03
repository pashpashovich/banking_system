package ru.clevertec.bank.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.clevertec.bank.request.CurrencyRate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CurrencyConversionService {

    private final RestTemplate restTemplate;

    public CurrencyConversionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal convert(BigDecimal amount, String fromCurrency, String toCurrency) {
        List<CurrencyRate> rates = getCurrencyRates();
        Map<String, BigDecimal> ratesMap = new HashMap<>();
        Map<String, Integer> scaleMap = new HashMap<>();

        for (CurrencyRate rate : rates) {
            ratesMap.put(rate.getCurAbbreviation(), rate.getCurOfficialRate());
            scaleMap.put(rate.getCurAbbreviation(), rate.getCurScale());
        }
        ratesMap.put("BYN", BigDecimal.ONE);
        if (!ratesMap.containsKey(fromCurrency)) {
            throw new IllegalArgumentException("Currency not found: " + fromCurrency);
        }

        BigDecimal baseAmount;
        if (!fromCurrency.equals("BYN")) {
            BigDecimal fromRate = ratesMap.get(fromCurrency);
            Integer fromScale = scaleMap.get(fromCurrency);
            if (fromRate == null || fromScale == null) {
                throw new IllegalArgumentException("Rate or scale not found for currency: " + fromCurrency);
            }
            baseAmount = amount.multiply(fromRate)
                    .divide(BigDecimal.valueOf(fromScale), BigDecimal.ROUND_HALF_UP);
        } else {
            baseAmount = amount;
        }

        BigDecimal convertedAmount;
        if (!toCurrency.equals("BYN")) {
            if (!ratesMap.containsKey(toCurrency)) {
                throw new IllegalArgumentException("Currency not found: " + toCurrency);
            }
            BigDecimal toRate = ratesMap.get(toCurrency);
            Integer toScale = scaleMap.get(toCurrency);
            if (toRate == null || toScale == null) {
                throw new IllegalArgumentException("Rate or scale not found for currency: " + toCurrency);
            }
            convertedAmount = baseAmount.divide(toRate, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(toScale));
        } else {
            convertedAmount = baseAmount;
        }

        return convertedAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private List<CurrencyRate> getCurrencyRates() {
        String url = "https://www.nbrb.by/api/exrates/rates?periodicity=0";
        CurrencyRate[] response = restTemplate.getForObject(url, CurrencyRate[].class);
        return List.of(response);
    }
}
