package com.neoscaler.cryptotrends.application.network.jobs;


import static com.neoscaler.cryptotrends.common.NotificationConstants.NOTIFICATION_CHANNEL_ALERTS_ID;
import static com.neoscaler.cryptotrends.common.NotificationConstants.NOTIFICATION_CHANNEL_ALERTS_NAME;

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
import com.google.common.collect.Lists;
import com.neoscaler.cryptotrends.CryptoTrendsApplication;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import com.neoscaler.cryptotrends.application.network.CoinpaprikaRemoteService;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.ticker.TickerResult;
import com.neoscaler.cryptotrends.application.network.util.RemoteException;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import com.neoscaler.cryptotrends.application.ui.MainActivity;
import com.neoscaler.cryptotrends.common.FiatCurrencyConfiguration;
import com.neoscaler.cryptotrends.common.NotificationConstants;
import com.neoscaler.cryptotrends.common.PriceFormatter;
import com.neoscaler.cryptotrends.common.SignalTypeIconResolver;
import com.neoscaler.cryptotrends.common.event.RemoteProblemEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import timber.log.Timber;

public class CheckCustomAlertsJob extends Job {

  public static final String TAG = "CHECK_CUSTOM_ALERTS";

  public static void scheduleBackgroundJob() {
    Timber.i("%s - scheduled in background", TAG);
    new JobRequest.Builder(TAG)
        .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
        .setPeriodic(TimeUnit.MINUTES.toMillis(30), TimeUnit.MINUTES.toMillis(5))
        .setUpdateCurrent(true)
        .build()
        .schedule();
  }

  public static void cancelAllScheduledJobs() {
    Timber.i("%s - scheduling canceled.", TAG);
    JobManager.instance().cancelAllForTag(TAG);
  }

  @NonNull
  @Override
  protected Result onRunJob(@NonNull Params params) {
    Timber.d("Job " + TAG + " runs...");

    CryptoTrendsApplication app = (CryptoTrendsApplication) getContext().getApplicationContext();
    DataRepository repository = app.getDataRepository();
    List<CustomAlert> alertList = repository.getActiveAlerts();

    if (alertList == null || alertList.size() == 0) {
      Timber.i("Nothing to check, no alerts saved. Aborting job.");
      // TODO Perhaps canceling all further jobs?
      return Result.SUCCESS;
    }

    // TODO Optimize later, only one API call regardless the alarms on the currency
/*        Set<String> currenciesToCheck = new HashSet<>();
        for (CustomAlert alert : alertList) {
            currenciesToCheck.add(alert.getCurrencyId());
        }*/

    for (CustomAlert alert : alertList) {
      try {
        TickerResult tickerResult = CoinpaprikaRemoteService.getInstance()
            .fetchCoinTicker(alert.getCurrencyId(), Lists.newArrayList(alert.getBaseCurrency()));

        double price = tickerResult.getQuotes().get(alert.getBaseCurrency()).getPrice();

        Timber.d("Checked current price for %s, its %s", alert.getCurrencyName(), price);

        // Fallback, it must be USD
        alert.setPriceLastChecked(price);
        alert.setLastChecked(DateTime.now());

        // Check if it fired
        if ((alert.getPriceBase() < alert.getPriceThresholdBaseCurrency()
            && alert.getPriceLastChecked() >= alert.getPriceThresholdBaseCurrency())
            || ((alert.getPriceBase() > alert.getPriceThresholdBaseCurrency()
            && alert.getPriceLastChecked() <= alert.getPriceThresholdBaseCurrency()))) {
          showNotification(alert);
          alert.setFiredAt(DateTime.now());
          alert.setActive(false);
        }

        // Finally, update in DB
        repository.updateAlert(alert);

      } catch (RemoteException e) {
        Timber.e(e, "Remote exception while updating currency for alert.");
        EventBus.getDefault().post(new RemoteProblemEvent("Error updating currencies for alerts."));
      }
    }

    return Result.SUCCESS;
  }

  private void showNotification(CustomAlert alert) {
    setupNotificationChannel();

    // TODO Perhaps link direct in AlertListFragment
    Intent resultIntent = new Intent(getContext(), MainActivity.class);
    PendingIntent resultPendingIntent =
        PendingIntent.getActivity(
            getContext(),
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        );

    int icon = SignalTypeIconResolver
        .resolveSignalTypeIconIdentifier(getContext(), alert.getSignalType());
    String currencySymbol = FiatCurrencyConfiguration.currencySymbolMap
        .get(alert.getBaseCurrency());
    String title;
    int color = 0;
    switch (alert.getSignalType()) {
      case BUY:
        title = String
            .format(getContext().getString(R.string.notification_customalert_triggered_title_buy),
                alert.getCurrencySymbol());
        color = getContext().getResources().getColor(R.color.primaryDarkColor);
        break;
      case SELL:
        title = String
            .format(getContext().getString(R.string.notification_customalert_triggered_title_sell),
                alert.getCurrencySymbol());
        color = getContext().getResources().getColor(R.color.secondaryDarkColor);
        break;
      default:
        title = String
            .format(getContext().getString(R.string.notification_customalert_triggered_title),
                alert.getCurrencySymbol());

    }

    // TODO Implement notification stacking

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(getContext(), "alerts")
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setContentText(String
                .format(getContext().getString(R.string.notification_customalert_triggered_text),
                    alert.getCurrencyName(),
                    PriceFormatter.formatPrice(alert.getPriceLastChecked(), true, currencySymbol),
                    PriceFormatter.formatPrice(alert.getPriceBase(), true, currencySymbol)))
            .setContentIntent(resultPendingIntent)
            .setChannelId("alerts");

    if (color != 0) {
      mBuilder.setColor(color);
    }

    NotificationManager mNotifyMgr =
        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    mNotifyMgr.notify(NotificationConstants.CUSTOM_ALERT, mBuilder.build());
  }

  @TargetApi(26)
  private void setupNotificationChannel() {
    if (Build.VERSION.SDK_INT >= 26) {
      NotificationChannel notificationChannel = new NotificationChannel(
          NOTIFICATION_CHANNEL_ALERTS_ID,
          NOTIFICATION_CHANNEL_ALERTS_NAME, NotificationManager.IMPORTANCE_LOW);
      notificationChannel.enableVibration(true);
      NotificationManager notificationManager =
          (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.createNotificationChannel(notificationChannel);
    }
  }
}
