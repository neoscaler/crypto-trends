package com.neoscaler.cryptotrends.common;

import android.graphics.Color;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.joda.time.DateTime;

public class ColorCalculator {

  private final static int MAX_ALPHA = 255;
  // Step size from 0 ... 255
  private final static int ALPHA_THRESHOLD = 40;
  // Changes visualized every x % of change value
  private static final int CHANGEVALUE_THRESHOLD = 5;

  private static final int MINUTES_UNTIL_OLD = 60;

  static int colorBackgroundGreen = Color.rgb(38, 166, 154);
  static int colorTextGreen = Color.rgb(0, 118, 108);

  static int colorBackgroundRed = Color.rgb(244, 67, 54);
  static int colorTextRed = Color.rgb(137, 27, 19);

  static int colorTextWhite = Color.WHITE;

  static int colorBackgroundGray = Color.GRAY;

  public static void calculateAndSetColors(TextView textView, double changeValue,
      DateTime lastUpdated) {
    // Calculate background alpha
    double absoluteChangeValue = Math.abs(changeValue);
    int alpha = ((int) absoluteChangeValue / CHANGEVALUE_THRESHOLD * ALPHA_THRESHOLD) + 10;
    if (alpha > MAX_ALPHA) {
      alpha = MAX_ALPHA;
    }

    // Set colors
    if (changeValue > 0) {
      // Green

      textView.setBackgroundColor(!isLastUpdatedOutdated(lastUpdated) ?
          ColorUtils.setAlphaComponent(colorBackgroundGreen, alpha) :
          ColorUtils.setAlphaComponent(colorBackgroundGray, alpha));
      if (alpha > 150) {
        textView.setTextColor(colorTextWhite);
      } else {
        textView.setTextColor(colorTextGreen);
      }

    } else {
      // Red
      textView.setBackgroundColor(!isLastUpdatedOutdated(lastUpdated) ?
          ColorUtils.setAlphaComponent(colorBackgroundRed, alpha) :
          ColorUtils.setAlphaComponent(colorBackgroundGray, alpha));

      if (alpha > 150) {
        textView.setTextColor(colorTextWhite);
      } else {
        textView.setTextColor(colorTextRed);
      }
    }
  }

  private static boolean isLastUpdatedOutdated(DateTime lastUpdated) {
    if (lastUpdated == null) {
      return true;
    }
    return lastUpdated.isBefore(DateTime.now().minusMinutes(MINUTES_UNTIL_OLD));
  }

}
