package com.neoscaler.cryptotrends.common.event;


public class DeleteAlertEvent {

  private long alertId;

  public DeleteAlertEvent(long alertId) {
    this.alertId = alertId;
  }

  public long getAlertId() {
    return alertId;
  }

  public void setAlertId(long alertId) {
    this.alertId = alertId;
  }
}
