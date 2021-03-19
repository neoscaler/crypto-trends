package com.neoscaler.cryptotrends.application.network.jobs;


import static com.neoscaler.cryptotrends.common.SharedPrefsKeys.SETTINGS_GENERAL_DISPLAYCURRENCY;

import androidx.annotation.NonNull;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.neoscaler.cryptotrends.application.network.CryptoCompareRemoteService;
import com.neoscaler.cryptotrends.application.network.api.cc.FullPriceDataResult;
import com.neoscaler.cryptotrends.application.network.util.RemoteException;
import com.neoscaler.cryptotrends.common.event.FullPriceDataReceivedEvent;
import com.neoscaler.cryptotrends.common.event.RemoteProblemEvent;
import com.pixplicity.easyprefs.library.Prefs;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class FetchFullPriceDataCcJob extends Job {

  public static final String TAG = "FETCH_FULL_PRICE_DATA";
  private static final String PARAM_CURRENCY_ID = "currencyId";
  private static final String PARAM_CURRENCY_SYMBOL = "currencySymbol";
  private static final String PARAM_CURRENCY_ID_SHORT = "currencyIdShort";

  public static void startJob(String id, String symbol) {
    PersistableBundleCompat extras = new PersistableBundleCompat();

    // CC has not symbol in ID
    String[] idWithoutSymbolSplit = id.split("-", 2);

    extras.putString(FetchFullPriceDataCcJob.PARAM_CURRENCY_ID, id);
    extras.putString(FetchFullPriceDataCcJob.PARAM_CURRENCY_ID_SHORT, idWithoutSymbolSplit[1]);
    extras.putString(FetchFullPriceDataCcJob.PARAM_CURRENCY_SYMBOL, symbol);

    new JobRequest.Builder(FetchFullPriceDataCcJob.TAG)
        .addExtras(extras)
        .startNow()
        .build()
        .schedule();
  }

  @NonNull
  @Override
  protected Result onRunJob(@NonNull Params params) {
    Timber.d("Job " + TAG + " runs...");
    PersistableBundleCompat extras = params.getExtras();

    try {
      String baseCurrency = Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD");
      FullPriceDataResult result = CryptoCompareRemoteService.getInstance()
          .fetchFullPriceData(extras.getString(PARAM_CURRENCY_SYMBOL, null), baseCurrency);
      // Send to repository
      FullPriceDataReceivedEvent event = new FullPriceDataReceivedEvent();
      event.setResult(result);
      event.setId(extras.getString(PARAM_CURRENCY_ID, null));
      event.setSymbol(extras.getString(PARAM_CURRENCY_SYMBOL, null));
      event.setFiatCurrency(baseCurrency);
      EventBus.getDefault().post(event);
    } catch (RemoteException e) {
      Timber.d("canceling job. reason: %s, throwable: %s", TAG, e);
      EventBus.getDefault()
          .post(new RemoteProblemEvent(
              "Error getting priceInformation globalDataResultContent from CryptoCompare API."));
      return Result.SUCCESS;
    }

    return Result.SUCCESS;
  }
}
