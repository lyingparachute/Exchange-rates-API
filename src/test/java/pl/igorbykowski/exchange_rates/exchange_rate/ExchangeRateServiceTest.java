package pl.igorbykowski.exchange_rates.exchange_rate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import pl.igorbykowski.exchange_rates.exchange_rate.difference.BidAskDifferenceResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.min_max.MinMaxAverageValueResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.ExchangeRateNBPResponse;
import pl.igorbykowski.exchange_rates.exchange_rate.nbp_api_response.RateNBPResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

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

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "XYZ")
        void throwsIllegalArgumentException_givenInvalidCurrencyCode(String currencyCode) {
            // Given
            LocalDate date = LocalDate.of(2023, 4, 24);

            // When, Then
            assertThatThrownBy(() -> service.getAverageExchangeRateByDateAndCurrency(currencyCode, date))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Wrong currencyCode: " + currencyCode);
        }

        @Test
        void throwsNoSuchElementException_givenNullResponseEntityBody() {
            // Given
            String currencyCode = "USD";
            LocalDate date = LocalDate.of(2023, 4, 24);
            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any()))
                    .thenReturn(ResponseEntity.ok(null));

            // When, Then
            assertThatThrownBy(() -> service.getAverageExchangeRateByDateAndCurrency(currencyCode, date))
                    .isExactlyInstanceOf(NoSuchElementException.class)
                    .hasMessage("Cannot get exchange rate response from received data.");
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

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "XYZ")
        void throwsIllegalArgumentException_givenInvalidCurrencyCode(String currencyCode) {
            // Given
            int numOfQuotes = 10;

            // When, Then
            assertThatThrownBy(() -> service.getMinMaxAverageValueForXDays(currencyCode, numOfQuotes))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wrong currencyCode: " + currencyCode);
        }

        @Test
        void throwsNoSuchElementException_givenNullResponseEntityBody() {
            // Given
            String currencyCode = "USD";
            int numOfQuotes = 10;

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any()))
                    .thenReturn(ResponseEntity.ok(null));

            // When, Then
            assertThatThrownBy(() -> service.getMinMaxAverageValueForXDays(currencyCode, numOfQuotes))
                    .isExactlyInstanceOf(NoSuchElementException.class)
                    .hasMessage("Cannot get exchange rate response from received data.");
        }
    }

    @Nested
    class GetMajorDifferenceBetweenBuyAndAskRate {
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
            BigDecimal expectedDifferenceResult = rateResponse3.ask().subtract(rateResponse3.bid());

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any()))
                    .thenReturn(ResponseEntity.ok(exchangeRateNBPResponse));

            // When
            BidAskDifferenceResponse response = service.getMajorDifferenceBetweenBuyAndAskRate(currencyCode, numOfQuotes);

            // Then
            verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any());
            assertThat(response.currencyCode()).isEqualTo(currency);
            assertThat(response.currencyName()).isEqualTo(currency.getDescription());
            assertThat(response.date()).isEqualTo(rateResponse3.effectiveDate());
            assertThat(response.majorDifference()).isEqualTo(expectedDifferenceResult);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "XYZ")
        void throwsIllegalArgumentException_givenInvalidCurrencyCode(String currencyCode) {
            // Given
            int numOfQuotes = 10;

            // When, Then
            assertThatThrownBy(() -> service.getMajorDifferenceBetweenBuyAndAskRate(currencyCode, numOfQuotes))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Wrong currencyCode: " + currencyCode);
        }

        @Test
        void throwsNoSuchElementException_givenNullResponseEntityBody() {
            // Given
            String currencyCode = "USD";
            int numOfQuotes = 10;

            when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(),
                    ArgumentMatchers.<ParameterizedTypeReference<ExchangeRateNBPResponse>>any()))
                    .thenReturn(ResponseEntity.ok(null));

            // When, Then
            assertThatThrownBy(() -> service.getMajorDifferenceBetweenBuyAndAskRate(currencyCode, numOfQuotes))
                    .isExactlyInstanceOf(NoSuchElementException.class)
                    .hasMessage("Cannot get exchange rate response from received data.");
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
                BigDecimal.valueOf(2.1),
                BigDecimal.valueOf(2.2)
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
                BigDecimal.valueOf(3.9)
        );
    }
}