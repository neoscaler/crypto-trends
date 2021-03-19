package com.neoscaler.cryptotrends.application.network.jobs;


import static com.neoscaler.cryptotrends.common.NotificationConstants.NOTIFICATION_CHANNEL_SMARTALARMS_ID;
import static com.neoscaler.cryptotrends.common.NotificationConstants.NOTIFICATION_CHANNEL_SMARTALARMS_NAME;
import static com.neoscaler.cryptotrends.common.SharedPrefsKeys.SETTINGS_GENERAL_DISPLAYCURRENCY;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.GlobalMarketData;
import com.neoscaler.cryptotrends.application.network.CoinpaprikaRemoteService;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.global.GlobalDataResult;
import com.neoscaler.cryptotrends.application.network.util.RemoteException;
import com.neoscaler.cryptotrends.application.smartalarm.GlobalMarketSmartAlarm;
import com.neoscaler.cryptotrends.application.smartalarm.SmartAlarmGlobalCapCheckResult;
import com.neoscaler.cryptotrends.application.ui.MainActivity;
import com.neoscaler.cryptotrends.common.FiatCurrencyConfiguration;
import com.neoscaler.cryptotrends.common.NotificationConstants;
import com.neoscaler.cryptotrends.common.PriceFormatter;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.neoscaler.cryptotrends.common.event.GlobalMarketCapUpdatedEvent;
import com.pixplicity.easyprefs.library.Prefs;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import timber.log.Timber;

public class GlobalMarketDataBackgroundSyncJob extends Job {

  public static final String TAG = "FETCH_GLOBAL_MARKET_DATA";

  public static void scheduleBackgroundJob() {
    Timber.i("Global market globalDataResultContent Job - scheduled in background");
    new JobRequest.Builder(GlobalMarketDataBackgroundSyncJob.TAG)
        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
        .setPeriodic(TimeUnit.MINUTES.toMillis(30), TimeUnit.MINUTES.toMillis(5))
        .setUpdateCurrent(true)
        .build()
        .schedule();
  }

  public static void cancelAllScheduledJobs() {
    Timber.i("Global market globalDataResultContent smart alarm job - scheduling canceled.");
    JobManager.instance().cancelAllForTag(GlobalMarketDataBackgroundSyncJob.TAG);
  }

  @NonNull
  @Override
  protected Result onRunJob(@NonNull Params params) {
    Timber.d("Job " + TAG + " runs...");

    // TODO Instead of recreating the modelmapper each time, use DI to inject existing mapper
    Converter<Integer, DateTime> timestampToDateTime = new AbstractConverter<Integer, DateTime>() {
      protected DateTime convert(Integer source) {
        return source == null ? null : new DateTime(source.intValue());
      }
    };
    ModelMapper mModelMapper = new ModelMapper();
    mModelMapper.addConverter(timestampToDateTime);

    try {
      String baseCurrency = Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD");

      GlobalDataResult globalResult = CoinpaprikaRemoteService.getInstance()
          .fetchGlobalMarketData();

      // map to internal structures
      GlobalMarketData newData = new GlobalMarketData();
      newData.setLastUpdated(new DateTime(globalResult.getLastUpdated() * 1000L));
      newData.setActiveCurrencies(globalResult.getCryptocurrenciesNumber());
      newData.setMarketCapPercentageBitcoin(globalResult.getBitcoinDominancePercentage());

      // TODO Map currency according to choice, but API does not offer other currencies
      newData.setTotalMarketCap(globalResult.getMarketCapUsd());
      newData.setTotal24hVolume(globalResult.getVolume24hUsd());
      newData.setCurrency("USD");

      // Always send to repository for persisting
      GlobalMarketCapUpdatedEvent globalMarketCapUpdatedEvent = new GlobalMarketCapUpdatedEvent();
      globalMarketCapUpdatedEvent.setGlobalMarketData(newData);
      EventBus.getDefault().post(globalMarketCapUpdatedEvent);

      if (Prefs.getBoolean(SharedPrefsKeys.SETTINGS_SMARTALARMS_GLOBALMARKET_ACTIVE, false)) {
        SmartAlarmGlobalCapCheckResult result = GlobalMarketSmartAlarm.check(newData);
        if (result.isAlarmTriggered()) {
          showNotification(result);
        }
      }

      return Result.SUCCESS;
    } catch (RemoteException e) {
      Timber.e(e);
      return Result.FAILURE;
    }
  }

  @TargetApi(26)
  private void setupNotificationChannel() {
    if (Build.VERSION.SDK_INT >= 26) {
      NotificationChannel notificationChannel = new NotificationChannel(
          NOTIFICATION_CHANNEL_SMARTALARMS_ID,
          NOTIFICATION_CHANNEL_SMARTALARMS_NAME, NotificationManager.IMPORTANCE_DEFAULT);
      notificationChannel.enableVibration(true);

      NotificationManager notificationManager =
          (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(notificationChannel);
      }
    }
  }

  private void showNotification(SmartAlarmGlobalCapCheckResult result) {
    setupNotificationChannel();

    Intent resultIntent = new Intent(getContext(), MainActivity.class);
    PendingIntent resultPendingIntent =
        PendingIntent.getActivity(
            getContext(),
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

    String currencySymbol = FiatCurrencyConfiguration.currencySymbolMap
        .get(result.getBaseCurrency());
    String capBformatted = PriceFormatter
        .formatPrice((double) result.getCurrentMarketCap() / 1000000000D, true, currencySymbol);

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_SMARTALARMS_ID)
            .setSmallIcon(result.getDirection() == SmartAlarmGlobalCapCheckResult.Direction.UP ?
                R.drawable.ic_trending_up_white_24dp : R.drawable.ic_trending_down_white_24dp)
            .setContentTitle(
                getContext().getString(R.string.notification_smartalarm_marketcap_title))
            .setAutoCancel(true)
            .setContentText(String
                .format(getContext().getString(R.string.notification_smartalarm_marketcap_text),
                    result.getChangedPercent(), capBformatted))
            .setContentIntent(resultPendingIntent)
            .setChannelId(NOTIFICATION_CHANNEL_SMARTALARMS_ID);

    NotificationManager mNotifyMgr =
        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    if (mNotifyMgr != null) {
      mNotifyMgr.notify(NotificationConstants.SMARTALARM_GLOBALMARKET, mBuilder.build());
    }
  }

}
