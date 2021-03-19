package com.neoscaler.cryptotrends.application.network.jobs;

import androidx.annotation.NonNull;
import com.evernote.android.job.JobRequest;

public class CheckCustomAlertsManuallyJob extends CheckCustomAlertsJob {

  public static final String TAG = "CHECK_CUSTOM_ALERTS_MANUALLY";

  public static void startJob() {
    new JobRequest.Builder(CheckCustomAlertsManuallyJob.TAG)
        .startNow()
        .build()
        .schedule();
  }

  @NonNull
  @Override
  protected Result onRunJob(@NonNull Params params) {
    Result result = super.onRunJob(params);

    // TODO Snack!

    return result;
  }
}
