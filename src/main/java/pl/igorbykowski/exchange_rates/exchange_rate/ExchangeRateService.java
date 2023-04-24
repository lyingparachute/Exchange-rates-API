package pl.igorbykowski.exchange_rates.exchange_rate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.igorbykowski.exchange_rates.exchange_rate.average.AverageExchangeRateResponse;
import pl.igorbykowski.exchange_rates.currency.Currency;
import pl.igorbykowski.exchange_rates.exchange_rate.average.AverageRate;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ExchangeRateService {
    private static final String NBP_API_URL = "http://api.nbp.pl/api/exchangerates/rates/%s/%s/%s";
    private final RestTemplate restTemplate = new RestTemplate();

    public AverageExchangeRateResponse getAverageExchangeRate(String currencyCode, LocalDate date) {
        String table = "A";
        String url = String.format(NBP_API_URL, table, getCurrencyCode(currencyCode), getDateString(date));
        ResponseEntity<AverageExchangeRateResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    public List<AverageExchangeRateResponse> getLatestExchangeRates(String currencyCode) {
        String table = "A";
        String url = String.format(NBP_API_URL + "last/", table, getCurrencyCode(currencyCode));
        ResponseEntity<List<AverageExchangeRateResponse>> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response.getBody();
    }

    public MinMaxAverageValueResponse getMinMaxAverageValue(String currencyCode, int topCount) {
        String table = "B";
        Currency currency = getCurrencyCode(currencyCode);
        String url = String.format(NBP_API_URL, table, currency + "/last", topCount);
        ResponseEntity<AverageExchangeRateResponse> response = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        List<BigDecimal> midRates = response.getBody()
                .rates()
                .stream()
                .map(AverageRate::mid)
                .toList();
        BigDecimal minValue = Collections.min(midRates);
        BigDecimal maxValue = Collections.max(midRates);

        return new MinMaxAverageValueResponse(currency, minValue, maxValue);
    }

    private String getDateString(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public double getAverageExchangeRateByDateAndCurrency(String table, LocalDate date, String currencyCode) throws JsonProcessingException {
        String url = String.format(NBP_API_URL, table, getCurrencyCode(currencyCode), getDateString(date), "/?format=json" );
        ObjectMapper objectMapper = new ObjectMapper();
        String response = restTemplate.getForObject(url, String.class);
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode ratesNode = rootNode.get("rates");
        if (ratesNode.size() == 0) {
            throw new RuntimeException("No exchange rate found for the given date and code code.");
        }
        double sum = 0;
        for (JsonNode rateNode : ratesNode) {
            double rate = rateNode.get("mid").asDouble();
            sum += rate;
        }
        return sum / ratesNode.size();
    }

//    public Map<String, Double> getMaxAndMinAverageExchangeRatesByCurrencyAndN(String currencyCode, int n) throws Exception {
//        String url = "http://api.nbp.pl/api/exchangerates/rates/A/" + currencyCode + "/last/" + n + "/?format=json";
//        ObjectMapper objectMapper = new ObjectMapper();
//        String response = restTemplate.getForObject(url, String.class);
//        JsonNode rootNode = objectMapper.readTree(response);
//        JsonNode ratesNode = rootNode.get("rates");
//        if (ratesNode.size() < 2) {
//            throw new Exception("Not enough exchange rates found for the given code code and n.");
//        }
//        double max = Double.MIN_VALUE;
//        double min = Double.MAX_VALUE;
//        for (JsonNode rateNode : ratesNode) {
//            double rate = rateNode.get("mid").asDouble();
//            if (rate > max) {
//                max = rate;
//            }
//            if (rate < min) {
//                min = rate;
//            }
//        }
//        Map<String, Double> result = new HashMap<>();
//        result.put("max", max);
//        result.put("min", min);
//        return result;
//    }
////
//    public double getMajorDifferenceBetweenBuyAndAskRatesByCurrencyAndN(String currencyCode, int n) throws Exception {
//        String url = "http://api.nbp.pl/api/exchangerates/rates/C/" + currencyCode + "/last/" + n + "/?format=json";
//        ObjectMapper objectMapper = new ObjectMapper();
//        String response = restTemplate.getForObject(url, String.class);
//        JsonNode rootNode = objectMapper.readTree(response);
//        JsonNode ratesNode = rootNode.get("rates");
//        if (ratesNode.size() < 2) {
//            throw new Exception("Not enough exchange rates found for the given code code and n.");
//        }
//        double maxDiff = Double.MIN_VALUE;
//        for (int i = 0; i < ratesNode.size() - 1; i++) {
//            JsonNode rateNode1 = ratesNode.get(i);
//            JsonNode rateNode2 = ratesNode.get(i + 1);
//            double buyRate1 = rateNode1.get("bid").asDouble();
//            double askRate1 = rateNode1.get("ask").asDouble();
//            double buyRate2 = rateNode2.get("bid").asDouble();
//            double askRate2 = rateNode2.get("ask").asDouble();
//            double diff = Math.abs(buyRate1 - askRate2);
//            if (diff > maxDiff) {
//                maxDiff = diff;
//            }
//            diff = Math.abs(askRate1 - buyRate2);
//            if (diff > maxDiff) {
//                maxDiff = diff;
//            }
//        }
//        return maxDiff;
//    }

    private Currency getCurrencyCode(String code) {
        if (isValidCurrencyCode(code))
            return Currency.valueOf(code);
        else throw new IllegalArgumentException("Wrong code code");
    }

    private boolean isValidCurrencyCode(String code) {
        return Arrays.stream(Currency.values())
                .anyMatch(c -> c.name().equals(code));
    }
}
