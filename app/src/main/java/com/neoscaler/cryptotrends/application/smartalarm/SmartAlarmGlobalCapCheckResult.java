package com.neoscaler.cryptotrends.application.smartalarm;

import lombok.Data;

@Data
public class SmartAlarmGlobalCapCheckResult {

  private boolean alarmTriggered;

  private Direction direction;

  private double oldMarketCap;

  private double currentMarketCap;

  private String baseCurrency;

  private double changedPercent;

  public enum Direction {UP, DOWN}
}
