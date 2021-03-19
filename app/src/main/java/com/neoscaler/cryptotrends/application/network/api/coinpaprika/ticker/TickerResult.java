package com.neoscaler.cryptotrends.application.network.api.coinpaprika.ticker;

import com.google.gson.annotations.SerializedName;
import java.util.Map;
import javax.annotation.Generated;
import lombok.Data;

@Generated("com.robohorse.robopojogenerator")
@Data
public class TickerResult {

  @SerializedName("symbol")
  private String symbol;

  @SerializedName("circulating_supply")
  private double circulatingSupply;

  @SerializedName("last_updated")
  private String lastUpdated;

  @SerializedName("total_supply")
  private double totalSupply;

  @SerializedName("name")
  private String name;

  @SerializedName("max_supply")
  private double maxSupply;

  @SerializedName("beta_value")
  private double betaValue;

  @SerializedName("rank")
  private int rank;

  @SerializedName("id")
  private String id;

  @SerializedName("quotes")
  private Map<String, TickerQuote> quotes;
}