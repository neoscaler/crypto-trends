package com.neoscaler.cryptotrends.application.ui.alert;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import java.util.List;


public class AlertListViewModel extends ViewModel {

  private LiveData<List<CustomAlert>> mCustomAlerts;

  public AlertListViewModel(DataRepository mDataRepository) {
    mCustomAlerts = mDataRepository.getAlertsLiveData();
  }

  public LiveData<List<CustomAlert>> getCustomAlerts() {
    return mCustomAlerts;
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
      return (T) new AlertListViewModel(mRepository);
    }
  }
}
