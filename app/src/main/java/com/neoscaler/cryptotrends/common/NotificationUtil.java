package com.neoscaler.cryptotrends.common;

import static com.neoscaler.cryptotrends.common.NotificationConstants.NOTIFICATION_CHANNEL_ALERTS_ID;
import static com.neoscaler.cryptotrends.common.NotificationConstants.NOTIFICATION_CHANNEL_ALERTS_NAME;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationUtil {

  @TargetApi(26)
  public static void setupNotificationChannel(Context context) {
    if (Build.VERSION.SDK_INT >= 26) {
      NotificationChannel notificationChannel = new NotificationChannel(
          NOTIFICATION_CHANNEL_ALERTS_ID,
          NOTIFICATION_CHANNEL_ALERTS_NAME, NotificationManager.IMPORTANCE_LOW);
      notificationChannel.enableVibration(true);
      NotificationManager notificationManager =
          (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.createNotificationChannel(notificationChannel);
    }
  }

}
