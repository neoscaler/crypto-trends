package com.neoscaler.cryptotrends.application.ui.alert;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.neoscaler.cryptotrends.application.model.CryptoCurrency;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyBasic;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import io.reactivex.Single;
import java.util.List;


public class EditAddAlertViewModel extends ViewModel {

  private LiveData<List<CurrencyBasic>> availableCurrencies;

  private Single<CryptoCurrency> selectedCurrency;

  private CryptoCurrency cachedCurrency;

  private LiveData<CustomAlert> customAlert;

  public EditAddAlertViewModel(DataRepository mDataRepository, long alertId) {
    availableCurrencies = mDataRepository.getAvailableCurrencies();
    if (alertId != -1) {
      // Load existing alert
      customAlert = mDataRepository.fetchAsyncAlert(alertId);
    }
  }

  public CryptoCurrency getCachedCurrency() {
    return cachedCurrency;
  }

  public void setCachedCurrency(CryptoCurrency cachedCurrency) {
    this.cachedCurrency = cachedCurrency;
  }

  public LiveData<List<CurrencyBasic>> getAvailableCurrencies() {
    return availableCurrencies;
  }

  public Single<CryptoCurrency> getSelectedCurrency() {
    return selectedCurrency;
  }

  public void setSelectedCurrency(Single<CryptoCurrency> selectedCurrency) {
    this.selectedCurrency = selectedCurrency;
  }

  public LiveData<CustomAlert> getCustomAlert() {
    return customAlert;
  }

  /**
   * A creator is used to inject the product ID into the ViewModel <p> This creator is to showcase
   * how to inject dependencies into ViewModels. It's not actually necessary in this case, as the
   * product ID can be passed in a public method.
   */
  public static class Factory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final DataRepository mRepository;

    private long alertId;

    public Factory(@NonNull DataRepository dataRepository, long id) {
      mRepository = dataRepository;
      alertId = id;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
      //noinspection unchecked
      return (T) new EditAddAlertViewModel(mRepository, alertId);
    }
  }
}
