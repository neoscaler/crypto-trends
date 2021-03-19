package com.neoscaler.cryptotrends.application.model;

import lombok.Data;

@Data
public class PercentChange {

  private Double percentChange1h;

  private Double percentChange24h;

  private Double percentChange7d;

  private Double percentChange14d;

  private Double percentChange30d;

  private Double percentChange1y;

}
