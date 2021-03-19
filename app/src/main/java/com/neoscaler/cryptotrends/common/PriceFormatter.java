package com.neoscaler.cryptotrends.common;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class PriceFormatter {

  public static String formatPrice(double price, boolean groupingUsed, String currencySymbol) {
    return formatPrice(price, groupingUsed, currencySymbol, null);
  }

  public static String formatPrice(double price, boolean groupingUsed, String currencySymbol,
      Locale locale) {
    int fractionDigits = 0;
    if (price <= 0.0001) {
      fractionDigits = 8;
    } else if (price <= 0.001) {
      fractionDigits = 6;
    } else if (price <= 0.1) {
      fractionDigits = 4;
    } else if (price <= 1000) {
      fractionDigits = 2;
    }

    NumberFormat numberFormat = NumberFormat
        .getInstance(locale != null ? locale : Locale.getDefault());
    numberFormat.setGroupingUsed(groupingUsed);
    numberFormat.setMaximumFractionDigits(fractionDigits);
    numberFormat.setMinimumFractionDigits(fractionDigits);
    if (currencySymbol != null) {
      return formatWithCurrencySymbol(currencySymbol, numberFormat.format(price));
    } else {
      return numberFormat.format(price);
    }
  }

  private static String formatWithCurrencySymbol(String currencySymbol, String numberFormatted) {
    if ("$".equals(currencySymbol) || "¥".equals(currencySymbol) || "₹".equals(currencySymbol)) {
      return String.format("%s %s", currencySymbol, numberFormatted);
    } else {
      return String.format("%s %s", numberFormatted, currencySymbol);
    }
  }

  public static String formatPercentage(long count) {
    DecimalFormat dfPercentage = (DecimalFormat) DecimalFormat.getInstance();
    if (count < 1000) {
      return dfPercentage.format(count) + "%";
    }
    int exp = (int) (Math.log(count) / Math.log(1000));
    return String.format("%s%c%%",
        dfPercentage.format(count / Math.pow(1000, exp)),
        "kMGTPE".charAt(exp - 1));
  }

  public static String formatPriceSatoshi(double price) {
    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
    df.setMinimumFractionDigits(8);
    df.setMaximumIntegerDigits(8);
    return String.format("%s Ƀ", df.format(price));
  }
}
