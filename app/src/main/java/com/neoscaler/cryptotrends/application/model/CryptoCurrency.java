package com.neoscaler.cryptotrends.application.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.neoscaler.cryptotrends.database.converter.DateTimeTypeConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

@Entity
@Data
@NoArgsConstructor
public class CryptoCurrency {

  @PrimaryKey
  @NonNull
  private String id;

  @NonNull
  private String symbol;

  @NonNull
  private String name;

  @NonNull
  private Long marketCapRank;

  private Double volume24h;

  private Double marketCap;

  private Double totalSupply;

  private Double circulatingSupply;

  @Embedded
  private PercentChange percentChange = new PercentChange();

  @Embedded
  private PriceInformation priceInformation = new PriceInformation();

  private double high24h;

  private double low24h;

  @TypeConverters({DateTimeTypeConverter.class})
  private DateTime lastUpdated;

}
