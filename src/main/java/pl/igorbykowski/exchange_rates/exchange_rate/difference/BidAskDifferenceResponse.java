package pl.igorbykowski.exchange_rates.exchange_rate.difference;

import lombok.Builder;
import pl.igorbykowski.exchange_rates.currency.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BidAskDifferenceResponse(
        Currency currencyCode,
        String currencyName,
        BigDecimal majorDifference,
        LocalDate date
) {
}
