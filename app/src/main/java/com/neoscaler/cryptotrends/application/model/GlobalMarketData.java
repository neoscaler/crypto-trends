package com.neoscaler.cryptotrends.application.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.neoscaler.cryptotrends.database.converter.DateTimeTypeConverter;
import lombok.Data;
import org.joda.time.DateTime;

@Entity
@Data
public class GlobalMarketData {

  @PrimaryKey
  // FIXED, always only one entry
  private long id = 1L;

  private double totalMarketCap;

  private double total24hVolume;

  /*
  The selected currency.
   */
  private String currency;

  private double marketCapPercentageBitcoin;

  private double marketCapPercentageEthereum;

  private int activeCurrencies;

  //FIXME Remove
  private int activeMarkets;

  @TypeConverters({DateTimeTypeConverter.class})
  private DateTime lastUpdated;

}
