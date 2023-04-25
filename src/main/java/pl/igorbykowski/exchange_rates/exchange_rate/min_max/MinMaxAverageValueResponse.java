package pl.igorbykowski.exchange_rates.exchange_rate.min_max;

import pl.igorbykowski.exchange_rates.currency.Currency;

import java.math.BigDecimal;

public record MinMaxAverageValueResponse(
        Currency currencyCode,
        String currencyName,
        BigDecimal minAvgValue,
        BigDecimal maxAvgValue
) {
}
