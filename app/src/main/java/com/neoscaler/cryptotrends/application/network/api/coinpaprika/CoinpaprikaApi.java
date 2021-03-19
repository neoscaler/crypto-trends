package com.neoscaler.cryptotrends.application.network.api.coinpaprika;

import com.andiag.retrocache.Cached;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.global.GlobalDataResult;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.ticker.TickerResult;
import java.util.List;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CoinpaprikaApi {

  @GET("global")
  Cached<GlobalDataResult> getGlobalData();

  @GET("tickers")
  Cached<List<TickerResult>> getTickerResults(
      @Query("quotes") String quotes
  );

  @GET("tickers/{coin}")
  Cached<TickerResult> getCoinTicker(
      @Path("coin") String coinId,
      @Query("quotes") String quotes
  );

}
