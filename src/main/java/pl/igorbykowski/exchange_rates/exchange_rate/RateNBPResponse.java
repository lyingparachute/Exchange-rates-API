package pl.igorbykowski.exchange_rates.exchange_rate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RateNBPResponse(
        String no,
        LocalDate effectiveDate,
        BigDecimal mid,
        BigDecimal bid,
        BigDecimal ask
) {
}