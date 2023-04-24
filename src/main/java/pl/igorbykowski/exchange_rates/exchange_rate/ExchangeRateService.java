package pl.igorbykowski.exchange_rates.exchange_rate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.igorbykowski.exchange_rates.currency.Currency;
import pl.igorbykowski.exchange_rates.exchange_rate.average.ExchangeRateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.average.RateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.difference.DiffBetweenBuyAndAskRateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {
    private static final String NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/%s/%s/%s";
    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateResponse getAverageExchangeRateByDateAndCurrency(String currencyCode, LocalDate date) {
        String table = "A";
        String url = String.format(NBP_API_URL, table, getCurrencyCode(currencyCode), getDateString(date));
        ResponseEntity<ExchangeRateResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    public MinMaxAverageValueResponse getMinMaxAverageValue(String currencyCode, int topCount) {
        String table = "B";
        Currency currency = getCurrencyCode(currencyCode);
        String url = String.format(NBP_API_URL, table, currency + "/last", topCount);

        ResponseEntity<ExchangeRateResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        List<BigDecimal> midRates = response.getBody()
                .rates()
                .stream()
                .map(RateResponse::mid)
                .toList();
        BigDecimal minValue = Collections.min(midRates);
        BigDecimal maxValue = Collections.max(midRates);

        return new MinMaxAverageValueResponse(currency, minValue, maxValue);
    }

    private String getDateString(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private Currency getCurrencyCode(String code) {
        if (isValidCurrencyCode(code))
            return Currency.valueOf(code);
        else throw new IllegalArgumentException("Wrong currencyCode currencyCode");
    }

    private boolean isValidCurrencyCode(String code) {
        return Arrays.stream(Currency.values())
                .anyMatch(c -> c.name().equals(code));
    }

    public DiffBetweenBuyAndAskRateResponse getMajorDifferenceBetweenBuyAndAskRate(String currencyCode, int quotations) {
        String table = "C";
        Currency currency = getCurrencyCode(currencyCode);
        String url = String.format(NBP_API_URL, table, currency + "/last", quotations);

        ResponseEntity<ExchangeRateResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        Map<LocalDate, BigDecimal> collect = response.getBody().rates().stream()
                .collect(Collectors.toMap(
                        RateResponse::effectiveDate,
                        rate -> rate.ask().subtract(rate.bid())
                ));
        Entry<LocalDate, BigDecimal> biggestDifference = collect.entrySet().stream()
                .max(Entry.comparingByValue())
                .get();
        return new DiffBetweenBuyAndAskRateResponse(currency, biggestDifference.getKey(), biggestDifference.getValue());
    }
}
