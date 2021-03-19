package com.neoscaler.cryptotrends.common;


import com.google.common.collect.ImmutableMap;

public final class FiatCurrencyConfiguration {

  public static String defaultBaseCurrency = "USD";

  public static ImmutableMap<String, String> currencySymbolMap = ImmutableMap.<String, String>builder()
      .put("USD", "$")
      .put("EUR", "€")
      .put("AUD", "$")
      .put("BRL", "R$")
      .put("CAD", "$")
      .put("CHF", "CHF")
      .put("CLP", "$")
      .put("CNY", "¥")
      .put("CZK", "Kč")
      .put("DKK", "kr")
      .put("GBP", "£")
      .put("HKD", "$")
      .put("HUF", "Ft")
      .put("IDR", "Rp")
      .put("ILS", "₪")
      .put("INR", "₹")
      .put("JPY", "¥")
      .put("KRW", "₩")
      .put("MXN", "$")
      .put("MYR", "RM")
      .put("NOK", "kr")
      .put("NZD", "$")
      .put("PHP", "₱")
      .put("PKR", "₨")
      .put("PLN", "zł")
      .put("RUB", "\u20BD")
      .put("SEK", "kr")
      .put("SGD", "$")
      .put("THB", "฿") //Bitcoin???
      .put("TRY", "₺")
      .put("TWD", "NT$")
      .put("ZAR", "R")
      .put("UAH", "₴")
      .put("NGN", "₦")
      .put("VND", "₫")
      .put("BOB", "Bs")
      .put("COP", "$")
      .put("PEN", "S/")
      .put("ARS", "$")
      .put("ISK", "kr")
      .build();

}
