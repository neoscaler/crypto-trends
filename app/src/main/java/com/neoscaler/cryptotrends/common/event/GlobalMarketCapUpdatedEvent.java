package com.neoscaler.cryptotrends.common.event;

import com.neoscaler.cryptotrends.application.model.GlobalMarketData;

public class GlobalMarketCapUpdatedEvent {

  private GlobalMarketData globalMarketData;

  public GlobalMarketData getGlobalMarketData() {
    return globalMarketData;
  }

  public void setGlobalMarketData(GlobalMarketData globalMarketData) {
    this.globalMarketData = globalMarketData;
  }
}
