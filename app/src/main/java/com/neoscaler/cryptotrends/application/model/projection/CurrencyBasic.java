package com.neoscaler.cryptotrends.application.model.projection;

import lombok.Data;

@Data
public class CurrencyBasic {

  private String id;

  private String symbol;

  private String name;

  private int marketCapRank;

}
