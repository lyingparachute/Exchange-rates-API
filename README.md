# Exchange-rates-API

This project is a simple RESTful web API that queries data from the Narodowy Bank Polski's public APIs and returns relevant information from them.

# Getting Started
## Prerequisites
* Java 17
* Spring-Boot 3.0.6
* Maven 4.0.0
* Docker (optional)

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
  mvn clean install -DskipTests
```

## Running the application
1. Start application server with docker-compose
    ```bash
    docker-compose up --build
    ```
    ***note** - it might take a while to pull and build docker images*
    ```
    press CTRL+C to stop the app
    ```
2. Run app - second time
   * START APP
     ```bash
     docker-compose start
     ```
   * STOP APP
      ```bash
     docker-compose stop
     ```
   * REMOVE NETWORK
     ```bash
     docker-compose down
     ```
### Alternative way of running app locally

Create docker image and run project with IntelliJ:

```bash
docker run -p 3307:3306 --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=classroom --rm -d mysql
```


## Test the application by making HTTP requests to the following endpoints:
### Operation 1: Average exchange rate
   * Endpoint:
        
            GET /exchanges/{currencyCode}/{date}
   * Example: 
   
          GET /exchanges/GBP/2023-01-02
   * Response: 

          { 
              "currencyCode": "GBP",
              "currencyName": "Pound Sterling", 
              "date": "2023-04-24", 
              "averageExchangeRate": 5.2176 
          }
### Operation 2: Max and min average value
   * Endpoint: 
   
         GET /quotes/{currencyCode}/{numOfQuotes}
   * Example: 
    
         GET /quotes/USD/10
   * Response: 

          { 
              "currencyCode": "USD",
              "currencyName": "US Dollar", 
              "minAvgValue": 3.6707, 
              "maxAvgValue": 4.0110 
          }
### Operation 3: Major difference between buy and ask rate
   * Endpoint: 
   
         GET /rates/{currencyCode}/{numOfQuotes}
   * Example: 
         
         GET /rates/EUR/10
   * Response: 

          { 
              "currencyCode": "EUR",
              "currencyName": "Euro", 
              "majorDifference": 0.0934, 
              "date": "2023-04-12" 
          }