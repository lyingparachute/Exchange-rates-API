package pl.igorbykowski.exchange_rates.exchange_rate.min_max;

import lombok.Builder;
import pl.igorbykowski.exchange_rates.currency.Currency;

import java.math.BigDecimal;

@Builder
public record MinMaxAverageValueResponse(
        Currency currencyCode,
        String currencyName,
        BigDecimal minAvgValue,
        BigDecimal maxAvgValue
) {
}
