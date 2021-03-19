package com.neoscaler.cryptotrends.common.event;

public class ToggleCurrencyUserStatusEvent {

  private String id;

  private StatusType statusType;

  public ToggleCurrencyUserStatusEvent(String id, StatusType statusType) {
    this.id = id;
    this.statusType = statusType;
  }

  public String getId() {
    return id;
  }

  public StatusType getStatusType() {
    return statusType;
  }

  public enum StatusType {
    FAVORITE, WATCHLIST
  }
}
