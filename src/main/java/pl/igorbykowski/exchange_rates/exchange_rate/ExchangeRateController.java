package pl.igorbykowski.exchange_rates.exchange_rate;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.igorbykowski.exchange_rates.exchange_rate.average.AverageExchangeRateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.difference.BidAskDifferenceResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService service;

    @GetMapping("average/{currencyCode}/{date}")
    public ResponseEntity<AverageExchangeRateResponse> getAverageExchangeRate(@PathVariable("currencyCode") String currencyCode,
                                                                              @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        return ResponseEntity.ok(
                service.getAverageExchangeRateByDateAndCurrency(currencyCode, date));
    }

    @GetMapping("/min-max/{currencyCode}/{numOfQuotes}")
    public ResponseEntity<MinMaxAverageValueResponse> getMinMaxAverageValue(@PathVariable("currencyCode") String currencyCode,
                                                                            @PathVariable("numOfQuotes") int numOfQuotes) {
        return ResponseEntity.ok(
                service.getMinMaxAverageValueForXDays(currencyCode, numOfQuotes));
    }

    @GetMapping("/difference/{currencyCode}/{numOfQuotes}")
    public ResponseEntity<BidAskDifferenceResponse> getMajorDifference(@PathVariable("currencyCode") String currencyCode,
                                                                       @PathVariable("numOfQuotes") int numOfQuotes) {
        return ResponseEntity.ok(
                service.getMajorDifferenceBetweenBuyAndAskRate(currencyCode, numOfQuotes));
    }
}
