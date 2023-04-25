package pl.igorbykowski.exchange_rates.exchange_rate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pl.igorbykowski.exchange_rates.currency.Currency;
import pl.igorbykowski.exchange_rates.exchange_rate.average.AverageExchangeRateResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.ExchangeRateNBPResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.RateNBPResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService service;

    @Mock
    private RestTemplate restTemplate;


    @Nested
    class GetAverageExchangeRateByDateAndCurrency {
        @Test
        void returnsAverageExchangeRate_givenValidCurrencyCode_andDate() {
            // Given
            String currencyCode = "USD";
            Currency currency = Currency.valueOf(currencyCode);
            LocalDate date = LocalDate.of(2023, 4, 26);

            RateNBPResponse rateNbpApiResponse1 = createRateNbpApiResponse1(date);
            ExchangeRateNBPResponse exchangeRateNBPResponse = createExchangeRateNbpApiResponse(
                    currency, List.of(rateNbpApiResponse1)
            );

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any()))
                    .thenReturn(ResponseEntity.ok(exchangeRateNBPResponse));

            // When
            AverageExchangeRateResponse response = service.getAverageExchangeRateByDateAndCurrency(currencyCode, date);

            // Then

            assertThat(response.currencyCode()).isEqualTo(currency);
            assertThat(response.date()).isEqualTo(date);
            assertThat(response.averageExchangeRate()).isEqualTo(rateNbpApiResponse1.mid());
        }

        @Test
        void throwsThrowNoSuchElementException_givenInvalidCurrencyCode() {
            // Given
            String currencyCode = "XYZ";
            LocalDate date = LocalDate.of(2023, 4, 24);

            // when, then
            assertThatThrownBy(() -> service.getAverageExchangeRateByDateAndCurrency(currencyCode, date))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wrong currencyCode: " + currencyCode);
        }
    }

    private ExchangeRateNBPResponse createExchangeRateNbpApiResponse(Currency currency, List<RateNBPResponse> rates) {
        return new ExchangeRateNBPResponse(currency, rates);
    }

    private RateNBPResponse createRateNbpApiResponse1(LocalDate date) {
        return new RateNBPResponse(
                "no.1.2.3",
                date,
                BigDecimal.valueOf(1.1),
                BigDecimal.valueOf(2.2),
                BigDecimal.valueOf(3.3)
        );
    }

    @Nested
    class GetMinMaxAverageValueForXDays {

    }

    @Nested
    class GetMajorDifferenceBetweenBuyAndAskRate {

    }
}