package pl.igorbykowski.exchange_rates.exchange_rate;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.igorbykowski.exchange_rates.exchange_rate.average.AverageExchangeRateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/exchanges")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService service;


    @GetMapping("average")
    public ResponseEntity<AverageExchangeRateResponse> getAverageExchangeRate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                              @RequestParam("currencyCode") String currencyCode) {
       
        return ResponseEntity.ok(service.getAverageExchangeRate(currencyCode, date));
    }

    @GetMapping("/min-max")
    public ResponseEntity<MinMaxAverageValueResponse> getMinMaxAverageValue(@RequestParam("currencyCode") String currencyCode,
                                                                            @RequestParam("quotations") int quotations) {
        return ResponseEntity.ok(service.getMinMaxAverageValue(currencyCode, quotations));
    }
//
//    @GetMapping("/difference")
//    public ResponseEntity<Double> getMajorDifference(@RequestParam("currencyCode") String currencyCode,
//                                                     @RequestParam("numberOfLastQuotations") int numberOfLastQuotations) {
//        Double majorDifference = service.getMajorDifference(currencyCode, numberOfLastQuotations);
//        return ResponseEntity.ok(majorDifference);
//    }
}
