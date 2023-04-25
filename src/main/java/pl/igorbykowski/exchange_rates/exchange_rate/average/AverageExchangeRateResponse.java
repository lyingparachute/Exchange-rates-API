package pl.igorbykowski.exchange_rates.exchange_rate.average;

import lombok.Builder;
import pl.igorbykowski.exchange_rates.currency.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AverageExchangeRateResponse(
        Currency currencyCode,
        String currencyName,
        LocalDate date,
        BigDecimal averageExchangeRate
) {
}
