package pl.igorbykowski.exchange_rates.exchange_rate.average;

import pl.igorbykowski.exchange_rates.currency.Currency;

import java.util.List;

public record ExchangeRateResponse(
        Currency currencyCode,
        List<RateResponse> rates
) {
}
