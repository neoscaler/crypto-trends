package com.neoscaler.cryptotrends.application.network.api.coinpaprika.global;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import javax.annotation.Generated;
import lombok.Data;

@Generated("com.robohorse.robopojogenerator")
@Data
public class GlobalDataResult {

  @SerializedName("market_cap_ath_value")
  private long marketCapAthValue;

  @SerializedName("bitcoin_dominance_percentage")
  private double bitcoinDominancePercentage;

  @SerializedName("last_updated")
  private int lastUpdated;

  @SerializedName("volume_24h_ath_date")
  private Date volume24hAthDate;

  @SerializedName("volume_24h_change_24h")
  private double volume24hChange24h;

  @SerializedName("volume_24h_ath_value")
  private long volume24hAthValue;

  @SerializedName("volume_24h_usd")
  private long volume24hUsd;

  @SerializedName("cryptocurrencies_number")
  private int cryptocurrenciesNumber;

  @SerializedName("market_cap_usd")
  private long marketCapUsd;

  @SerializedName("market_cap_ath_date")
  private Date marketCapAthDate;

  @SerializedName("market_cap_change_24h")
  private double marketCapChange24h;
}