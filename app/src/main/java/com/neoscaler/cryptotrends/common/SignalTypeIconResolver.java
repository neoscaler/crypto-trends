package com.neoscaler.cryptotrends.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.CustomAlert.SignalType;

public class SignalTypeIconResolver {

  public static Drawable resolveSignalTypeIcon(Context context, SignalType signalType) {
    Resources resources = context.getResources();
    Drawable signalTypeIcon;
    switch (signalType) {
      case BUY:
        signalTypeIcon = resources.getDrawable(resources.getIdentifier("basket_fill", "drawable",
            context.getPackageName()));
        signalTypeIcon.setColorFilter(resources.getColor(R.color.secondaryTextColor),
            PorterDuff.Mode.SRC_ATOP);

        break;
      case SELL:
        signalTypeIcon = resources.getDrawable(resources.getIdentifier("basket_unfill", "drawable",
            context.getPackageName()));
        signalTypeIcon.setColorFilter(resources.getColor(R.color.secondaryTextColor),
            PorterDuff.Mode.SRC_ATOP);
        break;
      default:
        signalTypeIcon = resources.getDrawable(resources.getIdentifier("alert_decagram", "drawable",
            context.getPackageName()));
        signalTypeIcon.setColorFilter(resources.getColor(R.color.secondaryTextColor),
            PorterDuff.Mode.SRC_ATOP);
    }
    return signalTypeIcon;
  }

  public static int resolveSignalTypeIconIdentifier(Context context, SignalType signalType) {
    Resources resources = context.getResources();
    switch (signalType) {
      case BUY:
        return resources.getIdentifier("basket_fill", "drawable",
            context.getPackageName());
      case SELL:
        return resources.getIdentifier("basket_unfill", "drawable",
            context.getPackageName());
      default:
        return resources.getIdentifier("alert_decagram", "drawable",
            context.getPackageName());
    }
  }

}
