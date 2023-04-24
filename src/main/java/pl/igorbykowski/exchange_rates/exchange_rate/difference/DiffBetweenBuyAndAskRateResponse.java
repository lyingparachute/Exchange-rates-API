package pl.igorbykowski.exchange_rates.exchange_rate.difference;

import pl.igorbykowski.exchange_rates.currency.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiffBetweenBuyAndAskRateResponse(
        Currency currencyCode,
        LocalDate date,
        BigDecimal majorDifference
) {
}
