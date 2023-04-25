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
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.ExchangeRateNBPResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.RateNBPResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
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
                    currency, List.of(rateNbpApiResponse1));

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any()))
                    .thenReturn(ResponseEntity.ok(exchangeRateNBPResponse));

            // When
            AverageExchangeRateResponse response = service.getAverageExchangeRateByDateAndCurrency(currencyCode, date);

            // Then
            assertThat(response.currencyCode()).isEqualTo(currency);
            assertThat(response.currencyName()).isEqualTo(currency.getDescription());
            assertThat(response.date()).isEqualTo(date);
            assertThat(response.averageExchangeRate()).isEqualTo(rateNbpApiResponse1.mid());
        }

        @Test
        void throwsThrowNoSuchElementException_givenInvalidCurrencyCode() {
            // Given
            String currencyCode = "XYZ";
            LocalDate date = LocalDate.of(2023, 4, 24);

            // When, Then
            assertThatThrownBy(() -> service.getAverageExchangeRateByDateAndCurrency(currencyCode, date))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wrong currencyCode: " + currencyCode);
        }
    }

    @Nested
    class GetMinMaxAverageValueForXDays {
        @Test
        void returnsAverageExchangeRate_givenValidCurrencyCode_andNumOfQuotes() {
            // Given
            String currencyCode = "USD";
            Currency currency = Currency.valueOf(currencyCode);
            int numOfQuotes = 10;

            RateNBPResponse rateResponse1 = createRateNbpApiResponse1(LocalDate.of(2023, 3, 20));
            RateNBPResponse rateResponse2 = createRateNbpApiResponse2(LocalDate.of(2023, 3, 21));
            RateNBPResponse rateResponse3 = createRateNbpApiResponse3(LocalDate.of(2023, 3, 22));
            ExchangeRateNBPResponse exchangeRateNBPResponse = createExchangeRateNbpApiResponse(
                    currency, List.of(rateResponse1, rateResponse2, rateResponse3));

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any()))
                    .thenReturn(ResponseEntity.ok(exchangeRateNBPResponse));

            // When
            MinMaxAverageValueResponse response = service.getMinMaxAverageValueForXDays(currencyCode, numOfQuotes);

            // Then
            verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any());
            assertThat(response.currencyCode()).isEqualTo(currency);
            assertThat(response.currencyName()).isEqualTo(currency.getDescription());
            assertThat(response.minAvgValue()).isEqualTo(rateResponse1.mid());
            assertThat(response.maxAvgValue()).isEqualTo(rateResponse3.mid());
        }

        @Test
        void throwsThrowNoSuchElementException_givenInvalidCurrencyCode() {
            // Given
            String currencyCode = "XYZ";
            int numOfQuotes = 10;

            // When, Then
            assertThatThrownBy(() -> service.getMinMaxAverageValueForXDays(currencyCode, numOfQuotes))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wrong currencyCode: " + currencyCode);
        }
    }

    @Nested
    class GetMajorDifferenceBetweenBuyAndAskRate {

    }

    private ExchangeRateNBPResponse createExchangeRateNbpApiResponse(Currency currency, List<RateNBPResponse> rates) {
        return new ExchangeRateNBPResponse(currency, rates);
    }

    private RateNBPResponse createRateNbpApiResponse1(LocalDate date) {
        return new RateNBPResponse(
                "no.1.2.3",
                date,
                BigDecimal.valueOf(1.1),
                BigDecimal.valueOf(2.1),
                BigDecimal.valueOf(3.1)
        );
    }

    private RateNBPResponse createRateNbpApiResponse2(LocalDate date) {
        return new RateNBPResponse(
                "no.1.2.3",
                date,
                BigDecimal.valueOf(1.2),
                BigDecimal.valueOf(2.2),
                BigDecimal.valueOf(3.2)
        );
    }

    private RateNBPResponse createRateNbpApiResponse3(LocalDate date) {
        return new RateNBPResponse(
                "no.1.2.3",
                date,
                BigDecimal.valueOf(1.3),
                BigDecimal.valueOf(2.3),
                BigDecimal.valueOf(3.3)
        );
    }
}