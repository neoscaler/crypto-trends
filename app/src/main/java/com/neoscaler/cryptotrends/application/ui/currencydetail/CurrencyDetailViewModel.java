package com.neoscaler.cryptotrends.application.ui.currencydetail;

import static com.neoscaler.cryptotrends.common.SharedPrefsKeys.SETTINGS_GENERAL_DISPLAYCURRENCY;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyListEntry;
import com.neoscaler.cryptotrends.application.network.CryptoCompareRemoteService;
import com.neoscaler.cryptotrends.application.network.api.cc.HistoResult;
import com.neoscaler.cryptotrends.application.network.jobs.FetchFullPriceDataCcJob;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import com.neoscaler.cryptotrends.common.event.RemoteProblemDetailEvent;
import com.pixplicity.easyprefs.library.Prefs;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

@Singleton
public class CurrencyDetailViewModel extends ViewModel {

  private DataRepository mDataRepository;

  private LiveData<CurrencyListEntry> currencyEntry;

  private MutableLiveData<DiagramTimespanResult> histoResult = new MutableLiveData<>();

  @Inject
  public CurrencyDetailViewModel(DataRepository mDataRepository, String id, String symbol) {
    this.mDataRepository = mDataRepository;
    currencyEntry = mDataRepository.fetchCurrency(id);

    // Get globalDataResultContent from CryptoCompare
    Timber.d(String
        .format("Updating full price information for %s from CryptoCompare",
            id));
    FetchFullPriceDataCcJob.startJob(id, symbol);

    // Get linechart globalDataResultContent
    String baseCurrency = Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD");
    Timber.d(String.format("Loading line chart values for %s from CryptoCompare", id));

    executeDiagramApiCall(symbol, baseCurrency, DiagramTimespanConfiguration.MONTH_3);
  }

  private void executeDiagramApiCall(String symbol, String baseCurrency,
      DiagramTimespanConfiguration config) {
    Single<HistoResult> result = CryptoCompareRemoteService.getInstance()
        .fetchHistoricalPriceData(symbol, baseCurrency, config);
    result.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(rxHistoResult -> histoResult
            .setValue(new DiagramTimespanResult(config, rxHistoResult)), throwable -> {
          // Handle error
          EventBus.getDefault()
              .post(new RemoteProblemDetailEvent(
                  "Error while updating priceInformation globalDataResultContent", throwable));
        });
  }


  public LiveData<CurrencyListEntry> getCurrencyEntry() {
    return currencyEntry;
  }

  public LiveData<DiagramTimespanResult> getDiagramData() {
    return histoResult;
  }

  public void refreshCurrencyDetailValues() {
    FetchFullPriceDataCcJob
        .startJob(currencyEntry.getValue().getId(), currencyEntry.getValue().getSymbol());
    executeDiagramApiCall(currencyEntry.getValue().getSymbol(),
        Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD"),
        // FIXME: Load current active Timespan
        DiagramTimespanConfiguration.MONTH_3);
  }

  public void reloadDiagramValues(DiagramTimespanConfiguration configuration) {
    executeDiagramApiCall(currencyEntry.getValue().getSymbol(),
        Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD"),
        configuration);
  }

  /**
   * A creator is used to inject the product ID into the ViewModel <p> This creator is to showcase
   * how to inject dependencies into ViewModels. It's not actually necessary in this case, as the
   * product ID can be passed in a public method.
   */
  public static class Factory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final DataRepository mRepository;

    private String id;

    private String symbol;

    public Factory(@NonNull DataRepository dataRepository, String id, String symbol) {
      mRepository = dataRepository;
      this.id = id;
      this.symbol = symbol;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
      //noinspection unchecked
      return (T) new CurrencyDetailViewModel(mRepository, id, symbol);
    }
  }
}
