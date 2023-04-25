package pl.igorbykowski.exchange_rates.exchange_rate.average;

import pl.igorbykowski.exchange_rates.currency.Currency;
import pl.igorbykowski.exchange_rates.exchange_rate.RateResponse;

import java.util.List;

public record AverageExchangeRateResponse(
        Currency currencyCode,
        List<AverageRateResponse> rates
) {
}
