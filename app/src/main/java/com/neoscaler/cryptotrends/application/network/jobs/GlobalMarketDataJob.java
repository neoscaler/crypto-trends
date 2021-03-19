package com.neoscaler.cryptotrends.application.network.jobs;

import com.evernote.android.job.JobRequest;

public class GlobalMarketDataJob extends GlobalMarketDataBackgroundSyncJob {

  public static final String TAG = "FETCH_GLOBAL_MARKET_DATA_MANUALLY";

  public static void startJob() {
    new JobRequest.Builder(GlobalMarketDataBackgroundSyncJob.TAG)
        .startNow()
        .build()
        .schedule();
  }
}
