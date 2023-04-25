package pl.igorbykowski.exchange_rates.exchange_rate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.igorbykowski.exchange_rates.currency.Currency;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.ExchangeRateNBPResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.RateNBPResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.average.AverageExchangeRateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.difference.BidAskDifferenceResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExchangeRateService {
    private static final String NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/%s/%s/%s";
    private final RestTemplate restTemplate = new RestTemplate();

    public AverageExchangeRateResponse getAverageExchangeRateByDateAndCurrency(String currencyCode, LocalDate date) {
        String table = "A";
        Currency currency = getCurrencyCode(currencyCode);
        String url = String.format(NBP_API_URL, table, currency, getDateString(date));
        ResponseEntity<ExchangeRateNBPResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        BigDecimal averageExchangeRate = Objects.requireNonNull(response.getBody()).rates().get(0).mid();
        return new AverageExchangeRateResponse(currency, currency.getDescription(), date, averageExchangeRate);
    }

    public MinMaxAverageValueResponse getMinMaxAverageValue(String currencyCode, int topCount) {
        String table = "B";
        Currency currency = getCurrencyCode(currencyCode);
        String url = String.format(NBP_API_URL, table, currency + "/last", topCount);

        ResponseEntity<ExchangeRateNBPResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        List<BigDecimal> midRates = response.getBody()
                .rates()
                .stream()
                .map(RateNBPResponse::mid)
                .toList();
        BigDecimal minValue = Collections.min(midRates);
        BigDecimal maxValue = Collections.max(midRates);

        return new MinMaxAverageValueResponse(currency, minValue, maxValue);
    }

    public BidAskDifferenceResponse getMajorDifferenceBetweenBuyAndAskRate(String currencyCode, int quotations) {
        String table = "C";
        Currency currency = getCurrencyCode(currencyCode);
        String url = String.format(NBP_API_URL, table, currency + "/last", quotations);

        ResponseEntity<ExchangeRateNBPResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        Map<LocalDate, BigDecimal> collect = response.getBody().rates().stream()
                .collect(Collectors.toMap(
                        RateNBPResponse::effectiveDate,
                        rate -> rate.ask().subtract(rate.bid())
                ));
        Map.Entry<LocalDate, BigDecimal> biggestDifference = collect.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get();
        return new BidAskDifferenceResponse(currency, biggestDifference.getKey(), biggestDifference.getValue());
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
}

