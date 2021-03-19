package com.neoscaler.cryptotrends.application.network.api.cc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Map;
import lombok.Data;

@Data
public class FullPriceDataResult {

  @SerializedName("RAW")
  @Expose
  private Map<String, Map<String, FullPriceDataEntry>> raw;
  @SerializedName("DISPLAY")
  @Expose
  private Map<String, Map<String, Map<String, String>>> display;

}