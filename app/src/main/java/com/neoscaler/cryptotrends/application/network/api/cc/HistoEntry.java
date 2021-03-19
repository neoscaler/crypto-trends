package com.neoscaler.cryptotrends.application.network.api.cc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class HistoEntry {

  @SerializedName("time")
  @Expose
  private Integer time;
  @SerializedName("close")
  @Expose
  private Double close;
  @SerializedName("high")
  @Expose
  private Double high;
  @SerializedName("low")
  @Expose
  private Double low;
  @SerializedName("open")
  @Expose
  private Double open;
  @SerializedName("volumefrom")
  @Expose
  private Double volumefrom;
  @SerializedName("volumeto")
  @Expose
  private Double volumeto;


}
