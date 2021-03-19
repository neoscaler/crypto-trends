package com.neoscaler.cryptotrends.common.event;

import com.neoscaler.cryptotrends.application.network.api.cc.FullPriceDataResult;

public class FullPriceDataReceivedEvent {

  private FullPriceDataResult result;

  private String id;

  private String symbol;

  private String fiatCurrency;

  public String getFiatCurrency() {
    return fiatCurrency;
  }

  public void setFiatCurrency(String fiatCurrency) {
    this.fiatCurrency = fiatCurrency;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public FullPriceDataResult getResult() {
    return result;
  }

  public void setResult(FullPriceDataResult result) {
    this.result = result;
  }

}
