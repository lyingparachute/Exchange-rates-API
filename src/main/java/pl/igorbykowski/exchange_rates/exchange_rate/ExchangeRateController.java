package pl.igorbykowski.exchange_rates.exchange_rate;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.igorbykowski.exchange_rates.exchange_rate.average.ExchangeRateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/exchanges")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService service;

    @GetMapping("average")
    public ResponseEntity<ExchangeRateResponse> getAverageExchangeRate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                       @RequestParam("currencyCode") String currencyCode) {
        return ResponseEntity.ok(
                service.getAverageExchangeRateByDateAndCurrency(currencyCode, date));
    }

    @GetMapping("/min-max")
    public ResponseEntity<MinMaxAverageValueResponse> getMinMaxAverageValue(@RequestParam("currencyCode") String currencyCode,
                                                                            @RequestParam("quotations") int quotations) {
        return ResponseEntity.ok(
                service.getMinMaxAverageValue(currencyCode, quotations));
    }

    @GetMapping("/rates/{currencyCode}/{numOfQuotes}")
    public ResponseEntity<DiffBetweenBuyAndAskRateResponse> getMajorDifference(@PathVariable("currencyCode") String currencyCode,
                                                                               @PathVariable("numOfQuotes") int numOfQuotes) {
        return ResponseEntity.ok(
                service.getMajorDifferenceBetweenBuyAndAskRate(currencyCode, numOfQuotes));
    }
}
