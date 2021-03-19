package com.neoscaler.cryptotrends.common.event;


public class ToggleStatusAlertEvent {

  private long alertId;

  public ToggleStatusAlertEvent(long alertId) {
    this.alertId = alertId;
  }

  public long getAlertId() {
    return alertId;
  }

  public void setAlertId(long alertId) {
    this.alertId = alertId;
  }
}
