package pl.igorbykowski.exchange_rates.exchange_rate;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pl.igorbykowski.exchange_rates.currency.Currency;
import pl.igorbykowski.exchange_rates.exchange_rate.difference.BidAskDifferenceResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/exchanges")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService service;

    @GetMapping("average")
    public ResponseEntity<ExchangeRateNBPResponse> getAverageExchangeRate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
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
    public ResponseEntity<BidAskDifferenceResponse> getMajorDifference(@PathVariable("currencyCode") String currencyCode,
                                                                       @PathVariable("numOfQuotes") int numOfQuotes) {
        return ResponseEntity.ok(
                service.getMajorDifferenceBetweenBuyAndAskRate(currencyCode, numOfQuotes));
    }


}
