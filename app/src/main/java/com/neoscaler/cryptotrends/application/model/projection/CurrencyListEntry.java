package com.neoscaler.cryptotrends.application.model.projection;

import lombok.Data;
import org.joda.time.DateTime;

/*
    POJO for displaying globalDataResultContent in CurrencyList. Loaded via DAO.
 */
@Data
public class CurrencyListEntry {

  // Market Cap GlobalDataResultContent

  private String id;

  private String symbol;

  private String name;

  private int marketCapRank;

  private double volume24h;

  private double marketCap;

  private double totalSupply;

  private double circulatingSupply;

  private double high24h;

  private double low24h;

  // PercentChange

  private double percentChange1h;

  private double percentChange24h;

  private double percentChange7d;

  private double percentChange14d;

  private double percentChange30d;

  private double percentChange1y;

  // PriceInformation

  private double priceCurrency;

  private double priceBtc;

  private String currency;

  private DateTime lastUpdated;

  // CurrencyUserData

  private boolean favorite;

  private boolean watched;

}
