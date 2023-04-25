package pl.igorbykowski.exchange_rates.exchange_rate.NBP_api_response;

import pl.igorbykowski.exchange_rates.currency.Currency;

import java.util.List;

public record ExchangeRateNBPResponse(
        Currency currencyCode,
        List<RateNBPResponse> rates
) {
}
