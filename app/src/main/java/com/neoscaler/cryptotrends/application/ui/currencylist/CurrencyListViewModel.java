package com.neoscaler.cryptotrends.application.ui.currencylist;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.neoscaler.cryptotrends.application.model.GlobalMarketData;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyListEntry;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.pixplicity.easyprefs.library.Prefs;
import java.util.List;

public class CurrencyListViewModel extends ViewModel {

  private LiveData<List<CurrencyListEntry>> persistedCurrencies;
  private MutableLiveData<List<CurrencyListEntry>> persistedCurrenciesFiltered = new MutableLiveData<>();

  private LiveData<GlobalMarketData> globalMarketData;

  private boolean initialPriceUpdateDone;

  private BottomTabView activeTab;

  public CurrencyListViewModel(DataRepository mDataRepository) {
    persistedCurrencies = mDataRepository.getCurrencies();
    persistedCurrenciesFiltered.setValue(persistedCurrencies.getValue());

    String activeTab = Prefs.getString(SharedPrefsKeys.CURRLIST_BOTTOMNAV_ACTIVE, "ALL");
    switch (activeTab) {
      case "FAVS":
        this.activeTab = BottomTabView.FAVORITE;
        break;
      case "WATCHED":
        this.activeTab = BottomTabView.WATCHLIST;
        break;
      case "ALL":
        this.activeTab = BottomTabView.ALL;
        break;
      default:
        throw new RuntimeException("Unsupported bottom tab type.");
    }
    globalMarketData = mDataRepository.getGlobalMarketData();
  }

  public boolean isInitialPriceUpdateDone() {
    return initialPriceUpdateDone;
  }

  public void setInitialPriceUpdateDone(boolean initialPriceUpdateDone) {
    this.initialPriceUpdateDone = initialPriceUpdateDone;
  }

  public LiveData<GlobalMarketData> getGlobalMarketData() {
    return globalMarketData;
  }

  public LiveData<List<CurrencyListEntry>> getPersistedCurrencies() {
    return persistedCurrencies;
  }

  public LiveData<List<CurrencyListEntry>> getPersistedCurrenciesFiltered() {
    return persistedCurrenciesFiltered;
  }

  public void setPersistedCurrenciesFiltered(List<CurrencyListEntry> persistedCurrenciesFiltered) {
    this.persistedCurrenciesFiltered.setValue(persistedCurrenciesFiltered);
  }

  public boolean isFavoriteOnly() {
    return activeTab.equals(BottomTabView.FAVORITE);
  }

  public boolean isWatchlistOnly() {
    return activeTab.equals(BottomTabView.WATCHLIST);
  }

  public BottomTabView getActiveTab() {
    return activeTab;
  }

  public void setActiveTab(BottomTabView activeTab) {
    this.activeTab = activeTab;
  }

  public enum BottomTabView {
    ALL, FAVORITE, WATCHLIST
  }

  /**
   * A creator is used to inject the product ID into the ViewModel <p> This creator is to showcase
   * how to inject dependencies into ViewModels. It's not actually necessary in this case, as the
   * product ID can be passed in a public method.
   */
  public static class Factory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final DataRepository mRepository;

    public Factory(@NonNull DataRepository dataRepository) {
      mRepository = dataRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
      //noinspection unchecked
      return (T) new CurrencyListViewModel(mRepository);
    }
  }
}
