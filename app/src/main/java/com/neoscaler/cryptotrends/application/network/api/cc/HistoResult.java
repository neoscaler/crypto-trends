package com.neoscaler.cryptotrends.application.network.api.cc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class HistoResult {

  @SerializedName("Response")
  @Expose
  private String response;
  @SerializedName("Type")
  @Expose
  private Integer type;
  @SerializedName("Aggregated")
  @Expose
  private Boolean aggregated;
  @SerializedName("Data")
  @Expose
  private List<HistoEntry> data = new ArrayList<>();
  @SerializedName("TimeTo")
  @Expose
  private Integer timeTo;
  @SerializedName("TimeFrom")
  @Expose
  private Integer timeFrom;
  @SerializedName("FirstValueInArray")
  @Expose
  private Boolean firstValueInArray;
  @SerializedName("ConversionType")
  @Expose
  private ConversionType conversionType;

}