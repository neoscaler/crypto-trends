package com.neoscaler.cryptotrends.application.ui.currencydetail;

import com.neoscaler.cryptotrends.application.network.api.cc.HistoResult;

public class DiagramTimespanResult {

  private DiagramTimespanConfiguration config;

  private HistoResult histoResult;

  public DiagramTimespanResult(
      DiagramTimespanConfiguration config,
      HistoResult histoResult) {
    this.config = config;
    this.histoResult = histoResult;
  }

  public DiagramTimespanConfiguration getConfig() {
    return config;
  }

  public HistoResult getHistoResult() {
    return histoResult;
  }
}
