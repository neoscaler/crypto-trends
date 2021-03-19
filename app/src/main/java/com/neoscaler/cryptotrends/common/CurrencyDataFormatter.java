package com.neoscaler.cryptotrends.common;

import android.content.Context;
import com.neoscaler.cryptotrends.R;

public class CurrencyDataFormatter {

  public static String formatCurrencyName(Context context, String originalName) {
    if (context == null || originalName == null) {
      throw new IllegalArgumentException("Parameters are not allowed to be null");
    }
    int maxTitleLength = (int) context.getResources().getDimension(R.dimen.text_currency_title_max);
    return originalName.length() > maxTitleLength ? originalName.substring(0, maxTitleLength - 1)
        + "..." : originalName;
  }
}
