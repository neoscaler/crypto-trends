package com.neoscaler.cryptotrends.application.network.api.cc;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class FullPriceDataEntry {

  @SerializedName("TYPE")
  @Expose
  private String type;
  @SerializedName("MARKET")
  @Expose
  private String market;
  @SerializedName("FROMSYMBOL")
  @Expose
  private String fromsymbol;
  @SerializedName("TOSYMBOL")
  @Expose
  private String tosymbol;
  @SerializedName("FLAGS")
  @Expose
  private String flags;
  @SerializedName("PRICE")
  @Expose
  private Double price;
  @SerializedName("LASTUPDATE")
  @Expose
  private Integer lastUpdate;
  @SerializedName("LASTVOLUME")
  @Expose
  private Double lastVolume;
  @SerializedName("LASTVOLUMETO")
  @Expose
  private Double lastVolumeTo;
  @SerializedName("LASTTRADEID")
  @Expose
  private String lastTradeId;
  @SerializedName("VOLUMEDAY")
  @Expose
  private Double volumeDay;
  @SerializedName("VOLUMEDAYTO")
  @Expose
  private Double volumeDayTo;
  @SerializedName("VOLUME24HOUR")
  @Expose
  private Double volume24Hour;
  @SerializedName("VOLUME24HOURTO")
  @Expose
  private Double volume24HourTo;
  @SerializedName("OPENDAY")
  @Expose
  private Double openDay;
  @SerializedName("HIGHDAY")
  @Expose
  private Double highDay;
  @SerializedName("LOWDAY")
  @Expose
  private Double lowDay;
  @SerializedName("OPEN24HOUR")
  @Expose
  private Double open24Hour;
  @SerializedName("HIGH24HOUR")
  @Expose
  private Double high24Hour;
  @SerializedName("LOW24HOUR")
  @Expose
  private Double low24Hour;
  @SerializedName("LASTMARKET")
  @Expose
  private String lastMarket;
  @SerializedName("CHANGE24HOUR")
  @Expose
  private Double change24Hour;
  @SerializedName("CHANGEPCT24HOUR")
  @Expose
  private Double changePct24Hour;
  @SerializedName("CHANGEDAY")
  @Expose
  private Double changeDay;
  @SerializedName("CHANGEPCTDAY")
  @Expose
  private Double changePctDay;
  @SerializedName("SUPPLY")
  @Expose
  private Double supply;
  @SerializedName("MKTCAP")
  @Expose
  private Double mktCap;
  @SerializedName("TOTALVOLUME24H")
  @Expose
  private Double totalVolume24H;
  @SerializedName("TOTALVOLUME24HTO")
  @Expose
  private Double totalVolume24HTo;

}

