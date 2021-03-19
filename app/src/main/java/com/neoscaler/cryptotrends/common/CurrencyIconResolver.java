package com.neoscaler.cryptotrends.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import java.util.Objects;
import timber.log.Timber;

public class CurrencyIconResolver {

  public static Drawable resolveCurrencyIcon(Context context, String currencyId) {
    Objects.requireNonNull(currencyId);
    Objects.requireNonNull(context);

    Resources resources = context.getResources();

    String filename = currencyId
        .replace("-", "_")
        .replace(".", "")
        .replaceAll("^([0-9])", "_$1");

    try {
      final int resourceId = resources.getIdentifier(filename, "drawable",
          context.getPackageName());
      return resources.getDrawable(resourceId);
    } catch (Resources.NotFoundException e) {
      Timber.w("Icon " + filename + " not found for currencyId " + currencyId);
      return null;
    }
  }

}
