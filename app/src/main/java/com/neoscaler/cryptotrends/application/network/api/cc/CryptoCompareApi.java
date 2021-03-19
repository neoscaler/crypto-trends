package com.neoscaler.cryptotrends.application.network.api.cc;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CryptoCompareApi {

  //https://min-api.cryptocompare.com/data/pricemultifull?fsyms=BTC&tsyms=USD,EUR
  @GET("pricemultifull")
  Call<FullPriceDataResult> getFullPriceData(
      @Query("fsyms") String symbol,
      @Query("tsyms") String targetCurrencies);

  //https://min-api.cryptocompare.com/data/histohour?fsym=BTC&tsym=USD&limit=90&aggregate=8
  @GET("histohour")
  Single<HistoResult> getHistoricalHourData(
      @Query("fsym") String symbol,
      @Query("tsym") String fiatCurrency,
      @Query("limit") int limit,
      @Query("aggregate") int aggregate
  );

  //https://min-api.cryptocompare.com/data/histoday?fsym=BTC&tsym=USD&limit=90&aggregate=8
  @GET("histoday")
  Single<HistoResult> getHistoricalDayData(
      @Query("fsym") String symbol,
      @Query("tsym") String fiatCurrency,
      @Query("limit") int limit,
      @Query("aggregate") int aggregate
  );

  //https://min-api.cryptocompare.com/data/histominute?fsym=BTC&tsym=USD&limit=90&aggregate=8
  @GET("histominute")
  Single<HistoResult> getHistoricalMinuteData(
      @Query("fsym") String symbol,
      @Query("tsym") String fiatCurrency,
      @Query("limit") int limit,
      @Query("aggregate") int aggregate
  );

}
