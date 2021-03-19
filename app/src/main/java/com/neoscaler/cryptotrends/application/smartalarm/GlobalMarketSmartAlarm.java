package com.neoscaler.cryptotrends.application.smartalarm;

import com.neoscaler.cryptotrends.application.model.GlobalMarketData;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.pixplicity.easyprefs.library.Prefs;
import org.joda.time.DateTime;
import timber.log.Timber;

public class GlobalMarketSmartAlarm {

  public static SmartAlarmGlobalCapCheckResult check(GlobalMarketData newData) {
    int percentThreshold = Prefs
        .getInt(SharedPrefsKeys.SETTINGS_SMARTALARMS_GLOBALMARKET_THRESHOLD, 2);
    Timber.d("SMART ALARM CHECK: Global market cap (Threshold %s%%)", percentThreshold);

    double lowerBoundary = 1D - ((double) percentThreshold / 100D);
    double upperBoundary = 1D + ((double) percentThreshold / 100D);
    Timber.d("LowerBoundary %s, UpperBoundary %s", lowerBoundary, upperBoundary);

    double savedMarketCap = Prefs.getDouble(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP, 0L);
    double lastUpdated = Prefs
        .getDouble(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP_LASTUPDATED, -1L);
    Timber.d("savedMarketCap %s, lastUpdated %s", savedMarketCap, lastUpdated);

    if (lastUpdated == -1) {
      // Never run, save values
      Timber
          .i("No globalDataResultContent saved, updating globalDataResultContent (global market cap + lastUpdated)...");
      updateDataPrefStore(newData);
      return new SmartAlarmGlobalCapCheckResult();
    } else {
      // Compare if change
      double ratio = (double) newData.getTotalMarketCap() / (double) savedMarketCap;
      Timber.d("Calculated ratio to base value is %s", ratio);
      if (ratio <= lowerBoundary || ratio >= upperBoundary) {
        Timber
            .i("*** SMART ALARM TRIGGERED: Global market cap! %s has a ratio of %s to base value %s.",
                newData.getTotalMarketCap(), ratio, savedMarketCap);
        SmartAlarmGlobalCapCheckResult result = new SmartAlarmGlobalCapCheckResult();
        result.setAlarmTriggered(true);
        result.setCurrentMarketCap(newData.getTotalMarketCap());
        result.setBaseCurrency(newData.getCurrency());
        result.setDirection(ratio <= lowerBoundary ? SmartAlarmGlobalCapCheckResult.Direction.DOWN
            : SmartAlarmGlobalCapCheckResult.Direction.UP);
        result.setOldMarketCap(savedMarketCap);
        result.setChangedPercent((ratio - 1) * 100);

        updateDataPrefStore(newData);
        return result;
      }

      if (newData.getLastUpdated().isAfter(DateTime.now().minusHours(24))) {
        Timber.d("Ratio threshold not reached in time, saving new base value...");
        updateDataPrefStore(newData);
      }
      return new SmartAlarmGlobalCapCheckResult();
    }
  }

  private static void updateDataPrefStore(GlobalMarketData newData) {
    Prefs.putDouble(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP,
        newData.getTotalMarketCap());
    Prefs.putDouble(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP_LASTUPDATED,
        newData.getLastUpdated().getMillis());
  }
}
