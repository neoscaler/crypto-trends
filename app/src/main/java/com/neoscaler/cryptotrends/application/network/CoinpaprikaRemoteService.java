package com.neoscaler.cryptotrends.application.network;

import android.text.TextUtils;
import com.andiag.retrocache.Cached;
import com.andiag.retrocache.CachedCallAdapterFactory;
import com.andiag.retrocache.RetroCache;
import com.andiag.retrocache.cache.EntryCountSizeOf;
import com.iagocanalejas.dualcache.DualCache;
import com.neoscaler.cryptotrends.BuildConfig;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.CoinpaprikaApi;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.global.GlobalDataResult;
import com.neoscaler.cryptotrends.application.network.api.coinpaprika.ticker.TickerResult;
import com.neoscaler.cryptotrends.application.network.util.RemoteException;
import com.neoscaler.cryptotrends.common.LiveDataCallAdapterFactory;
import java.io.IOException;
import java.util.List;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class CoinpaprikaRemoteService {

  private static CoinpaprikaApi api;

  private static CoinpaprikaRemoteService instance;

  private static DualCache<String, byte[]> mCache = RetroCache.getBuilder(BuildConfig.VERSION_CODE)
      .useReferenceInRam(40960, new EntryCountSizeOf()).useVolatileCache(300L).noDisk().build();

  private CoinpaprikaRemoteService() {
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
    Dispatcher dispatcher = new Dispatcher();
    dispatcher.setMaxRequests(5);
    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor)
        .dispatcher(dispatcher).build();

    api = new Retrofit.Builder()
        .baseUrl("https://api.coinpaprika.com/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(new LiveDataCallAdapterFactory())
        .addCallAdapterFactory(new CachedCallAdapterFactory(mCache))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(CoinpaprikaApi.class);
  }

  public static synchronized CoinpaprikaRemoteService getInstance() {
    if (instance == null) {
      instance = new CoinpaprikaRemoteService();
    }
    return instance;
  }

  public CoinpaprikaApi getApi() {
    return api;
  }

  public GlobalDataResult fetchGlobalMarketData() throws RemoteException {
    Cached<GlobalDataResult> call = api.getGlobalData();

    Response<GlobalDataResult> response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      Timber.e("I/O exception while fetching global market data " + e
          .getMessage());
      throw new RemoteException(response);
    }

    if (response == null || !response.isSuccessful() || response.errorBody() != null) {
      throw new RemoteException(response);
    }

    return response.body();
  }

  public List<TickerResult> fetchTickers(List<String> quotes) throws RemoteException {
    Cached<List<TickerResult>> call = api.getTickerResults(TextUtils.join(",", quotes));

    Response<List<TickerResult>> response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      Timber.e("I/O exception while fetching ticker results " + e
          .getMessage());
      throw new RemoteException(response);
    }

    if (response == null || !response.isSuccessful() || response.errorBody() != null) {
      throw new RemoteException(response);
    }

    return response.body();
  }

  public TickerResult fetchCoinTicker(String coinId, List<String> quotes) throws RemoteException {
    Cached<TickerResult> call = api.getCoinTicker(coinId, TextUtils.join(",", quotes));

    Response<TickerResult> response = null;
    try {
      response = call.execute();
    } catch (IOException e) {
      Timber.e("I/O exception while fetching ticker results " + e
          .getMessage());
      throw new RemoteException(response);
    }

    if (response == null || !response.isSuccessful() || response.errorBody() != null) {
      throw new RemoteException(response);
    }
    return response.body();
  }


}
