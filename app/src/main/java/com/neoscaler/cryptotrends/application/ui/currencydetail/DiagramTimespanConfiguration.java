package com.neoscaler.cryptotrends.application.ui.currencydetail;

public enum DiagramTimespanConfiguration {

  /*
  90 days, 24h aggregated.
   */
  HOUR(HistoMode.HISTOMINUTE, 30, 2, false),
  DAY(HistoMode.HISTOMINUTE, 48, 30, false),
  WEEK(HistoMode.HISTOHOUR, 56, 3, false),
  MONTH(HistoMode.HISTOHOUR, 60, 12, false),
  MONTH_3(HistoMode.HISTODAY, 90, 1, true),
  MONTH_6(HistoMode.HISTODAY, 90, 2, true),
  YEAR(HistoMode.HISTODAY, 90, 4, true);

  private HistoMode mode;

  private int valueCount;

  private int aggregateHours;

  private boolean showYAxisFromZero;

  DiagramTimespanConfiguration(HistoMode mode, int valueCount, int aggregate,
      boolean showYAxisFromZero) {
    this.valueCount = valueCount;
    this.aggregateHours = aggregate;
    this.mode = mode;
    this.showYAxisFromZero = showYAxisFromZero;
  }

  public HistoMode getMode() {
    return mode;
  }

  public int getValueCount() {
    return valueCount;
  }

  public int getAggregateHours() {
    return aggregateHours;
  }

  public boolean isShowYAxisFromZero() {
    return showYAxisFromZero;
  }
}

