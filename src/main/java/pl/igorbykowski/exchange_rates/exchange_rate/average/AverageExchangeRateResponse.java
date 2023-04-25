package pl.igorbykowski.exchange_rates.exchange_rate.average;

import pl.igorbykowski.exchange_rates.currency.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AverageExchangeRateResponse(
        Currency currencyCode,
        String currencyName,
        LocalDate date,
        BigDecimal averageExchangeRate
) {
}
