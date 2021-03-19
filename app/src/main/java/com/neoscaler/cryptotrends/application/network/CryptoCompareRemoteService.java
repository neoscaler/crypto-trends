package com.neoscaler.cryptotrends.application.network;

import com.neoscaler.cryptotrends.application.network.api.cc.CryptoCompareApi;
import com.neoscaler.cryptotrends.application.network.api.cc.FullPriceDataResult;
import com.neoscaler.cryptotrends.application.network.api.cc.HistoResult;
import com.neoscaler.cryptotrends.application.network.util.RemoteException;
import com.neoscaler.cryptotrends.application.ui.currencydetail.DiagramTimespanConfiguration;
import com.neoscaler.cryptotrends.common.LiveDataCallAdapterFactory;
import io.reactivex.Single;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class CryptoCompareRemoteService {

  public static final String BASE_URL = "https://min-api.cryptocompare.com/data/";

  private static CryptoCompareApi cryptoCompareApi;

  private static CryptoCompareApi cryptoCompareApiRxJava;

  private static CryptoCompareRemoteService instance;

  public CryptoCompareRemoteService() {

    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

    cryptoCompareApi = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(new LiveDataCallAdapterFactory())
        .build()
        .create(CryptoCompareApi.class);

    cryptoCompareApiRxJava = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build().create(CryptoCompareApi.class);
  }

  public static synchronized CryptoCompareRemoteService getInstance() {
    if (instance == null) {
      instance = new CryptoCompareRemoteService();
    }
    return instance;
  }

  public FullPriceDataResult fetchFullPriceData(String cryptoSymbol, String fiatCurrency)
      throws RemoteException {
    String targetCurrencies = "BTC," + fiatCurrency;
    Call<FullPriceDataResult> call = cryptoCompareApi
        .getFullPriceData(cryptoSymbol, targetCurrencies);

    Response<FullPriceDataResult> response = null;
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

  public Single<HistoResult> fetchHistoricalPriceData(String cryptoSymbol,
      String fiatCurrency, DiagramTimespanConfiguration configuration) {

    Single<HistoResult> result = null;
    switch (configuration.getMode()) {
      case HISTOMINUTE:
        result = cryptoCompareApiRxJava
            .getHistoricalMinuteData(cryptoSymbol, fiatCurrency, configuration.getValueCount(),
                configuration.getAggregateHours());
        break;
      case HISTOHOUR:
        result = cryptoCompareApiRxJava
            .getHistoricalHourData(cryptoSymbol, fiatCurrency, configuration.getValueCount(),
                configuration.getAggregateHours());
        break;
      case HISTODAY:
        result = cryptoCompareApiRxJava
            .getHistoricalDayData(cryptoSymbol, fiatCurrency, configuration.getValueCount(),
                configuration.getAggregateHours());
        break;
    }

    return result;
  }

}
