# Exchange-rates-API

This project is a simple RESTful web API that queries data from the Narodowy Bank Polski's public APIs and returns relevant information from them.
The API supports three operations:
1. Average exchange rate given currency code and date (formatted YYYY-MM-DD).
2. Max and min average exchange rate value given a currency code and the number of last quotations N (where N <= 255).
3. Major difference between buy and ask exchange rate given a currency code and the number of last quotations N (where N <= 255).

# Getting Started
## Prerequisites
* Java 17
* Spring-Boot 3.0.6
* Maven 4.0.0

## Installing
### 1. Clone this repository

```bash
  git clone https://github.com/lyingparachute/Exchange-rates-API.git
```

Go to project directory

```bash
  cd Exchange-rates-API
```

### 2. Build project and perform tests

* Open terminal in project directory and run:
```bash
  mvn clean install
```

## Running the application
### 1. Start application in IntelliJ or in terminal:
```bash
mvn spring-boot:run
```

```
press CTRL+C to stop the app
```

## Test the application by making HTTP requests to the following endpoints:
### Operation 1: Average exchange rate given currency code and date (formatted YYYY-MM-DD)
   * Endpoint:
        
          GET /api/v1/exchange-rates/average/{currencyCode}/{date}
   * Example: 
   
          GET /api/v1/exchange-rates/GBP/2023-01-02
   * Response: 

          { 
              "currencyCode": "GBP",
              "currencyName": "Pound Sterling", 
              "date": "2023-04-24", 
              "averageExchangeRate": 5.2176 
          }
### Operation 2: Max and min average value given currency code and the number of last quotations N (N <= 255)
   * Endpoint: 
   
         GET /api/v1/exchange-rates/min-max/{currencyCode}/{numOfQuotes}
   * Example: 
    
         GET /api/v1/exchange-rates/min-max/USD/10
   * Response: 

          { 
              "currencyCode": "USD",
              "currencyName": "US Dollar", 
              "minAvgValue": 4.1905, 
              "maxAvgValue": 4.2917 
          }
### Operation 3: Major difference between buy and ask rate given currency code and the number of last quotations N (N <= 255)
   * Endpoint: 
   
         GET /api/v1/exchange-rates/difference/{currencyCode}/{numOfQuotes}
   * Example: 
         
         GET /api/v1/exchange-rates/difference/EUR/10
   * Response: 

          { 
              "currencyCode": "EUR",
              "currencyName": "Euro", 
              "majorDifference": 0.0934, 
              "date": "2023-04-12" 
          }