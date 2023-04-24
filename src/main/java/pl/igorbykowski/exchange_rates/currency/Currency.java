package pl.igorbykowski.exchange_rates.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    AUD("1 AUD", "Australian Dollar"),
    THB("1 THB", "Baht"),
    BRL("1 BRL", "Brazilian Real"),
    BGN("1 BGN", "Bulgarian Lev"),
    CAD("1 CAD", "Canadian Dollar"),
    CLP("100 CLP", "Chilean Peso"),
    CZK("1 CZK", "Czech Koruna"),
    DKK("1 DKK", "Danish Krone"),
    EUR("1 EUR", "Euro"),
    HUF("100 HUF", "Forint"),
    HKD("1 HKD", "Hong Kong Dollar"),
    UAH("1 UAH", "Hryvnia"),
    ISK("100 ISK", "Iceland Krona"),
    INR("100 INR", "Indian Rupee"),
    MYR("1 MYR", "Malaysian Ringgit"),
    MXN("1 MXN", "Mexican Peso"),
    ILS("1 ILS", "New Israeli Shekel"),
    NZD("1 NZD", "New Zealand Dollar"),
    NOK("1 NOK", "Norwegian Krone"),
    PHP("1 PHP", "Philippine Peso"),
    GBP("1 GBP", "Pound Sterling"),
    ZAR("1 ZAR", "Rand"),
    RON("1 RON", "Romanian Leu"),
    IDR("10000 IDR", "Rupiah"),
    SGD("1 SGD", "Singapore Dollar"),
    SEK("1 SEK", "Swedish Krona"),
    CHF("1 CHF", "Swiss Franc"),
    TRY("1 TRY", "Turkish Lira"),
    USD("1 USD", "US Dollar"),
    KRW("100 KRW", "Won"),
    JPY("100 JPY", "Yen"),
    CNY("1 CNY", "Yuan Renminbi"),
    XDR("1 XDR", "SDR Int'l Monetary Fund (I.M.F.)");

    private final String code;
    private final String description;
}
