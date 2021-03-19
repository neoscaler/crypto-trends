package com.neoscaler.cryptotrends;

import android.content.ContextWrapper;
import android.provider.Settings;
import android.widget.Toast;
import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.application.network.CryptoTrendsJobCreator;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.neoscaler.cryptotrends.infrastructure.configuration.DaggerAppComponent;
import com.pixplicity.easyprefs.library.Prefs;
import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import javax.inject.Inject;
import net.danlew.android.joda.JodaTimeAndroid;
import timber.log.Timber;

public class CryptoTrendsApplication extends DaggerApplication {

  @Inject
  DataRepository dataRepository;

  @Override
  protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().application(this).build();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    setupServices();
    disableAnalyticsIfInTestlab();
    disableAnalyticsIfDisabled();
  }

  private void disableAnalyticsIfDisabled() {
    if(!Prefs.getBoolean(SharedPrefsKeys.SETTINGS_GENERAL_ANALYTICS_ACTIVE, true)) {
      FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
      mFirebaseAnalytics.setAnalyticsCollectionEnabled(false);
      Timber.i("Analytics collection disabled due to settings.");
    }
  }

  private void disableAnalyticsIfInTestlab() {
    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    String testLabSetting =
        Settings.System.getString(getContentResolver(), "firebase.test.lab");
    if ("true".equals(testLabSetting)) {
      //You are running in Test Lab
      mFirebaseAnalytics.setAnalyticsCollectionEnabled(false);  //Disable Analytics Collection
      Toast.makeText(getApplicationContext(), "Disabling Analytics Collection ", Toast.LENGTH_SHORT)
          .show();
    }
  }

  public DataRepository getDataRepository() {
    return dataRepository;
  }

  // TODO Move to Dagger module
  private void setupServices() {
    JodaTimeAndroid.init(this);

    Stetho.initializeWithDefaults(this);

    // Initialize Timber
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }

    // Initialize the Prefs class
    new Prefs.Builder()
        .setContext(this)
        .setMode(ContextWrapper.MODE_PRIVATE)
        .setPrefsName(getPackageName())
        .setUseDefaultSharedPreference(true)
        .build();

    // Initialize JobManager
    JobManager.create(this).addJobCreator(new CryptoTrendsJobCreator());
  }

}
