package com.neoscaler.cryptotrends.application.network.jobs;


import android.app.NotificationManager;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.google.common.collect.Lists;
import com.neoscaler.cryptotrends.application.model.CryptoCurrency;
import com.neoscaler.cryptotrends.application.network.CoinpaprikaRemoteService;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.ticker.TickerQuote;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.ticker.TickerResult;
import com.neoscaler.cryptotrends.application.network.util.RemoteException;
import com.neoscaler.cryptotrends.common.NotificationConstants;
import com.neoscaler.cryptotrends.common.NotificationUtil;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.neoscaler.cryptotrends.common.event.CurrencyListUpdatedEvent;
import com.neoscaler.cryptotrends.common.event.CurrencyListUpdatedStartedEvent;
import com.neoscaler.cryptotrends.common.event.RemoteProblemEvent;
import com.pixplicity.easyprefs.library.Prefs;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import timber.log.Timber;

public class FetchTickerResultsJob extends Job {

  public static final String TAG = "FETCH_TICKER_RESULTS_COINPAPRIKA";

  private static final String BTC = "BTC";
  private NotificationManager mNotifyMgr;

  public static void startJob() {
    new JobRequest.Builder(FetchTickerResultsJob.TAG)
        .setUpdateCurrent(true)
        .startNow()
        .build()
        .schedule();
  }

  @NonNull
  @Override
  protected Result onRunJob(@NonNull Params params) {
    Timber.d("Job " + TAG + " runs...");

    try {
      mNotifyMgr = (NotificationManager) getContext()
          .getSystemService(Context.NOTIFICATION_SERVICE);
      EventBus.getDefault().post(new CurrencyListUpdatedStartedEvent());

      // TODO Disabled for now, not needed
      //this.showNotification();

      // Load
      String displayCurrency = Prefs
          .getString(SharedPrefsKeys.SETTINGS_GENERAL_DISPLAYCURRENCY, "USD");
      List<String> quotes = Lists.newArrayList(displayCurrency, BTC);
      List<TickerResult> results = CoinpaprikaRemoteService.getInstance().fetchTickers(quotes);

      // Transform
      List<CryptoCurrency> mappedResults = mapTicker2CryptoCurrency(results, displayCurrency);

      // Send to repository
      CurrencyListUpdatedEvent currencyListUpdatedEvent = new CurrencyListUpdatedEvent();
      currencyListUpdatedEvent.setTickerResultList(mappedResults);
      EventBus.getDefault().post(currencyListUpdatedEvent);

    } catch (RemoteException e) {
      Timber.d("canceling job. reason: %s, throwable: %s", TAG, e);
      EventBus.getDefault()
          .post(
              new RemoteProblemEvent(
                  "Error getting data from CoinPaprika API."));
      return Result.FAILURE;
    } finally {
      // TODO Disabled for now, not needed
      //mNotifyMgr.cancel(NotificationConstants.UPDATE_PRICES_NOTIFICATION);
    }

    return Result.SUCCESS;
  }

  private List<CryptoCurrency> mapTicker2CryptoCurrency(List<TickerResult> tickerResultList,
      String displayCurrency) {
    // Convert in app model globalDataResultContent
    List<CryptoCurrency> currencyList = new ArrayList<>();
    for (TickerResult tickerResult : tickerResultList) {
      // ID, Name, Symbol
      CryptoCurrency cryptoCurrency = new CryptoCurrency();
      cryptoCurrency.setId(tickerResult.getId());
      cryptoCurrency.setName(tickerResult.getName());
      cryptoCurrency.setSymbol(tickerResult.getSymbol().toUpperCase());
      cryptoCurrency.setMarketCapRank(
          tickerResult.getRank() != 0 ? tickerResult.getRank() : Long.MAX_VALUE);

      cryptoCurrency.setTotalSupply(tickerResult.getTotalSupply());
      cryptoCurrency.setCirculatingSupply(tickerResult.getCirculatingSupply());

      if (tickerResult.getLastUpdated() != null) {
        cryptoCurrency.setLastUpdated(new DateTime(tickerResult.getLastUpdated()));
      }

      TickerQuote fiatQuote = tickerResult.getQuotes().get(displayCurrency);
      if (fiatQuote != null) {
        cryptoCurrency.getPriceInformation()
            .setPriceCurrency(fiatQuote.getPrice());
        cryptoCurrency.getPriceInformation().setCurrency(displayCurrency);

        cryptoCurrency.getPercentChange()
            .setPercentChange1h(fiatQuote.getPercentChange1h());
        cryptoCurrency.getPercentChange()
            .setPercentChange24h(fiatQuote.getPercentChange24h());
        cryptoCurrency.getPercentChange()
            .setPercentChange7d(fiatQuote.getPercentChange7d());
        // TODO Other changes

        cryptoCurrency.setMarketCap(fiatQuote.getMarketCap());
        cryptoCurrency.setVolume24h(fiatQuote.getVolume24h());
      } else {
        Timber.e("Can't map currency, null objects");
      }

      TickerQuote btcQuote = tickerResult.getQuotes().get(BTC);
      if (btcQuote != null) {
        cryptoCurrency.getPriceInformation().setPriceBtc(btcQuote.getPrice());
      }

      currencyList.add(cryptoCurrency);
    }
    return currencyList;
  }

  private void showNotification() {
    NotificationUtil.setupNotificationChannel(getContext());

    int icon = getContext().getResources().getIdentifier("chart_line_white", "drawable",
        getContext().getPackageName());

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(getContext(), "alerts")
            .setSmallIcon(icon)
            .setContentTitle("Updating price data")
            .setAutoCancel(false)
            .setContentText("Please stand by...")
            .setChannelId("alerts")
            .setOngoing(true)
            .setProgress(1, 1, true);

    mNotifyMgr.notify(NotificationConstants.UPDATE_PRICES_NOTIFICATION, mBuilder.build());
  }
}
