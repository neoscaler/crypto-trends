package com.neoscaler.cryptotrends.application.network.api.coinpaprika.ticker;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Generated;
import lombok.Data;

@Generated("com.robohorse.robopojogenerator")
@Data
public class TickerQuote {

  @SerializedName("percent_change_1y")
  private double percentChange1y;

  @SerializedName("volume_24h_change_24h")
  private double volume24hChange24h;

  @SerializedName("ath_price")
  private double athPrice;

  @SerializedName("percent_from_price_ath")
  private double percentFromPriceAth;

  @SerializedName("market_cap_change_24h")
  private double marketCapChange24h;

  @SerializedName("percent_change_12h")
  private double percentChange12h;

  @SerializedName("percent_change_30d")
  private double percentChange30d;

  @SerializedName("percent_change_1h")
  private double percentChange1h;

  @SerializedName("market_cap")
  private double marketCap;

  @SerializedName("percent_change_24h")
  private double percentChange24h;

  @SerializedName("price")
  private double price;

  @SerializedName("volume_24h")
  private double volume24h;

  @SerializedName("percent_change_7d")
  private double percentChange7d;

  @SerializedName("ath_date")
  private String athDate;
}