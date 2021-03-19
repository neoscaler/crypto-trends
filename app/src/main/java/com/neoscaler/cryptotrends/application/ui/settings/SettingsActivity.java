package com.neoscaler.cryptotrends.application.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.network.jobs.GlobalMarketDataBackgroundSyncJob;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.pixplicity.easyprefs.library.Prefs;
import de.mateware.snacky.Snacky;
import timber.log.Timber;


public class SettingsActivity extends AppCompatActivity {

  private FirebaseAnalytics mFirebaseAnalytics;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    mFirebaseAnalytics
        .setCurrentScreen(this, getClass().getSimpleName(), getClass().getSimpleName());

    PreferenceManager.getDefaultSharedPreferences(this)
        .registerOnSharedPreferenceChangeListener(spChanged);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if (getFragmentManager().getBackStackEntryCount() > 0) {
      getFragmentManager().popBackStack();
    } else {
      super.onBackPressed();
    }
  }

  SharedPreferences.OnSharedPreferenceChangeListener spChanged = (sharedPreferences, key) -> {
    switch (key) {
      case SharedPrefsKeys.SETTINGS_SMARTALARMS_GLOBALMARKET_ACTIVE: {
        boolean alarmActive = Prefs
            .getBoolean(SharedPrefsKeys.SETTINGS_SMARTALARMS_GLOBALMARKET_ACTIVE, false);
        Timber.d("Smart alarm market cap status changed, now %s", alarmActive);

        if (alarmActive) {
          GlobalMarketDataBackgroundSyncJob.scheduleBackgroundJob();
          Snacky.builder().setActivity(this)
              .setBackgroundColor(getResources().getColor(R.color.primaryDarkColor))
              .setTextColor(getResources().getColor(R.color.snackTextColor))
              .setIcon(R.drawable.ic_alarm_white_24dp)
              .setText(R.string.smartalarm_marketcap_activated)
              .setDuration(Snacky.LENGTH_SHORT)
              .build()
              .show();
        } else {
          GlobalMarketDataBackgroundSyncJob.cancelAllScheduledJobs();
          Snacky.builder().setActivity(this)
              .setBackgroundColor(getResources().getColor(R.color.primaryDarkColor))
              .setTextColor(getResources().getColor(R.color.snackTextColor))
              .setIcon(R.drawable.ic_alarm_off_white_24dp)
              .setText(R.string.smartalarm_marketcap_disabled)
              .setDuration(Snacky.LENGTH_SHORT)
              .build()
              .show();
        }
      }
      case SharedPrefsKeys.SETTINGS_GENERAL_DISPLAYCURRENCY: {
        Timber
            .d("Base currency setting changed, deleting global market globalDataResultContent cache...");
        Prefs.remove(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP_LASTUPDATED);
        Prefs.remove(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP);
      }
      case SharedPrefsKeys.SETTINGS_GENERAL_ANALYTICS_ACTIVE: {
        boolean analyticsState = Prefs
            .getBoolean(SharedPrefsKeys.SETTINGS_GENERAL_ANALYTICS_ACTIVE, true);
        Timber.d("Analytics settings changed to %b.", analyticsState);
        if (!analyticsState) {
          mFirebaseAnalytics.logEvent("analytics_collection_disabled", null);
          mFirebaseAnalytics.setAnalyticsCollectionEnabled(analyticsState);
        } else {
          mFirebaseAnalytics.setAnalyticsCollectionEnabled(analyticsState);
          mFirebaseAnalytics.logEvent("analytics_collection_enabled", null);
        }

      }
    }
  };

}
