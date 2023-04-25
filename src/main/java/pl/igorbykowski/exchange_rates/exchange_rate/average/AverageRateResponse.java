package pl.igorbykowski.exchange_rates.exchange_rate.average;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AverageRateResponse(
        String no,
        LocalDate effectiveDate,
        BigDecimal mid
) {

}
