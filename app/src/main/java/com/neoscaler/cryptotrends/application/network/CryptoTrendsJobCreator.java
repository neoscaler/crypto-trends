package com.neoscaler.cryptotrends.application.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.neoscaler.cryptotrends.application.network.jobs.CheckCustomAlertsJob;
import com.neoscaler.cryptotrends.application.network.jobs.CheckCustomAlertsManuallyJob;
import com.neoscaler.cryptotrends.application.network.jobs.FetchFullPriceDataCcJob;
import com.neoscaler.cryptotrends.application.network.jobs.FetchTickerResultsJob;
import com.neoscaler.cryptotrends.application.network.jobs.GlobalMarketDataBackgroundSyncJob;
import com.neoscaler.cryptotrends.application.network.jobs.GlobalMarketDataJob;

public class CryptoTrendsJobCreator implements JobCreator {

  @Nullable
  @Override
  public Job create(@NonNull String tag) {
    switch (tag) {
      case FetchFullPriceDataCcJob.TAG:
        return new FetchFullPriceDataCcJob();
      case GlobalMarketDataBackgroundSyncJob.TAG:
        return new GlobalMarketDataBackgroundSyncJob();
      case GlobalMarketDataJob.TAG:
        return new GlobalMarketDataJob();
      case FetchTickerResultsJob.TAG:
        return new FetchTickerResultsJob();
      case CheckCustomAlertsJob.TAG:
        return new CheckCustomAlertsJob();
      case CheckCustomAlertsManuallyJob.TAG:
        return new CheckCustomAlertsManuallyJob();
      default:
        return null;
    }
  }
}
