package com.neoscaler.cryptotrends.application.repository;

import androidx.lifecycle.LiveData;
import com.neoscaler.cryptotrends.application.model.CryptoCurrency;
import com.neoscaler.cryptotrends.application.model.CurrencyUserData;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import com.neoscaler.cryptotrends.application.model.GlobalMarketData;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyBasic;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyListEntry;
import com.neoscaler.cryptotrends.application.network.api.cc.FullPriceDataEntry;
import com.neoscaler.cryptotrends.application.network.jobs.CheckCustomAlertsJob;
import com.neoscaler.cryptotrends.common.event.CurrencyListUpdatedEvent;
import com.neoscaler.cryptotrends.common.event.DeleteAlertEvent;
import com.neoscaler.cryptotrends.common.event.FullPriceDataReceivedEvent;
import com.neoscaler.cryptotrends.common.event.GlobalMarketCapUpdatedEvent;
import com.neoscaler.cryptotrends.common.event.SaveAlertEvent;
import com.neoscaler.cryptotrends.common.event.ToggleCurrencyUserStatusEvent;
import com.neoscaler.cryptotrends.common.event.ToggleStatusAlertEvent;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CryptoCurrencyDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CurrencyUserDataDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CustomAlertDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.GlobalMarketDataDao;
import io.reactivex.Single;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import timber.log.Timber;

// TODO Refactor many DAO redirects. Other solution needed.
public class DataRepository {

  private final CryptoCurrencyDao mCryptoCurrencyDao;

  private final GlobalMarketDataDao mGlobalMarketDataDao;

  private final CurrencyUserDataDao mCurrencyUserDataDao;

  private final CustomAlertDao mCustomAlertDao;

  private ModelMapper mModelMapper;

  @Inject
  public DataRepository(CryptoCurrencyDao cryptoCurrencyDao,
      GlobalMarketDataDao globalMarketDataDao, CurrencyUserDataDao currencyUserDataDao,
      CustomAlertDao customAlertDao, ModelMapper modelMapper) {
    mGlobalMarketDataDao = globalMarketDataDao;
    mCryptoCurrencyDao = cryptoCurrencyDao;
    mCurrencyUserDataDao = currencyUserDataDao;
    mCustomAlertDao = customAlertDao;
    mModelMapper = modelMapper;

    EventBus.getDefault().register(this);
  }

  public LiveData<CurrencyListEntry> fetchCurrency(String id) {
    Timber.i("Load currency from db...");
    return mCryptoCurrencyDao.fetchCurrency(id);
  }

  public Single<CryptoCurrency> loadFullCurrencyAsync(String id) {
    return mCryptoCurrencyDao.loadFullCurrencyAsync(id);
  }

  public LiveData<List<CurrencyListEntry>> getCurrencies() {
    Timber.i("Load currencies from db...");
    return mCryptoCurrencyDao.loadAllCurrencies();
  }

  public LiveData<List<CurrencyBasic>> getAvailableCurrencies() {
    return mCryptoCurrencyDao.getAvailableCurrencies();
  }

  public LiveData<List<CustomAlert>> getAlertsLiveData() {
    Timber.i("Load alerts from DB...");
    return mCustomAlertDao.getAlertsLiveData();
  }

  public List<CustomAlert> getActiveAlerts() {
    Timber.i("Load active alerts from DB...");
    return mCustomAlertDao.getActiveAlerts();
  }

  public void updateAlert(CustomAlert alert) {
    mCustomAlertDao.update(alert);

    // Check if no alert present and active, shutdown background job
    List<CustomAlert> alerts = mCustomAlertDao.getActiveAlerts();
    if (alerts.size() == 0) {
      Timber.i("No active alerts, unscheduling background job.");
      CheckCustomAlertsJob.cancelAllScheduledJobs();
    }
  }

  public LiveData<GlobalMarketData> getGlobalMarketData() {
    Timber.i("Load global market globalDataResultContent from db...");
    return mGlobalMarketDataDao.loadMostCurrent();
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageToggleCurrencyUserStatus(ToggleCurrencyUserStatusEvent event) {
    String id = event.getId();
    ToggleCurrencyUserStatusEvent.StatusType statusType = event.getStatusType();

    CurrencyUserData currUserData = mCurrencyUserDataDao.getCurrencyUserData(id);
    if (currUserData != null) {
      if (statusType == ToggleCurrencyUserStatusEvent.StatusType.FAVORITE) {
        currUserData.setFavorite(!currUserData.isFavorite());
      } else if (statusType == ToggleCurrencyUserStatusEvent.StatusType.WATCHLIST) {
        currUserData.setWatched(!currUserData.isWatched());
      }
      mCurrencyUserDataDao.updateCurrencyUserData(currUserData);
    } else {
      // Not present, so its false
      CurrencyUserData currency = new CurrencyUserData();
      currency.setId(id);
      if (statusType == ToggleCurrencyUserStatusEvent.StatusType.FAVORITE) {
        currency.setFavorite(true);
      } else if (statusType == ToggleCurrencyUserStatusEvent.StatusType.WATCHLIST) {
        currency.setWatched(true);
      }
      mCurrencyUserDataDao.insertNewCurrencyUserData(currency);
    }

  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageDeleteAlert(DeleteAlertEvent event) {
    long id = event.getAlertId();
    Timber.d("Deleting alert with ID %s in DB.", id);
    CustomAlert customAlert = new CustomAlert();
    customAlert.setId(id);
    mCustomAlertDao.deleteAlert(customAlert);

    // Check if no other alert present and active, shutdown background job
    List<CustomAlert> alerts = mCustomAlertDao.getActiveAlerts();
    if (alerts.size() == 0) {
      Timber.i("No active alerts, unscheduling background job.");
      CheckCustomAlertsJob.cancelAllScheduledJobs();
    }
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageToggleStatusAlert(ToggleStatusAlertEvent event) {
    long id = event.getAlertId();
    Timber.d("Toggle active/inactive status of ID %s in DB.", id);

    CustomAlert alert = mCustomAlertDao.getAlert(id);
    if (alert != null) {
      if (alert.isActive()) {
        alert.setActive(false);
      } else {
        alert.setActive(true);
      }
      mCustomAlertDao.update(alert);
    }

    // Check if no other alert present and active, shutdown background job
    List<CustomAlert> alerts = mCustomAlertDao.getActiveAlerts();
    if (alerts.size() == 0) {
      Timber.i("No active alerts, unscheduling background job.");
      CheckCustomAlertsJob.cancelAllScheduledJobs();
    } else {
      Timber.i("Active alerts, scheduling background job.");
      CheckCustomAlertsJob.scheduleBackgroundJob();
    }
  }


  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageEventGlobalData(GlobalMarketCapUpdatedEvent event) {
    Timber.d("Updated global globalDataResultContent received, saving in DB.");
    // insert in db
    mGlobalMarketDataDao.insert(event.getGlobalMarketData());
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageEventSaveAlert(SaveAlertEvent event) {
    Timber.d("New/updated alert received, saving in DB.");

    CustomAlert alert = event.getNewCustomAlert();

    if (alert.getId() == 0) {
      // Load and fill values which are not entered in GUI
      CryptoCurrency currency = mCryptoCurrencyDao.loadFullCurrency(alert.getCurrencyId());
      if (currency != null) {
        alert.setCurrencyName(currency.getName());
        alert.setCurrencySymbol(currency.getSymbol());
        alert.setPriceLastChecked(currency.getPriceInformation().getPriceCurrency());

        mCustomAlertDao.insert(alert);
        // Activate background job for checking alerts
        // FIXME Check if already scheduled, then do nothing
        CheckCustomAlertsJob.scheduleBackgroundJob();
      } else {
        // TODO Send error snack saving alert
        Timber.w("Could not save custom alert for %s.", alert.getCurrencyId());
      }
    } else {
      mCustomAlertDao.update(alert);
    }
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageEventTickerResults(CurrencyListUpdatedEvent event) {
    Timber.d("Updated ticker results received, saving in DB.");

    // insert in db
    mCryptoCurrencyDao.deleteAllCurrencies();
    mCryptoCurrencyDao.insertCurrencies(event.getTickerResultList());
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND)
  public void onMessageEventTickerResults(FullPriceDataReceivedEvent event) {
    Timber.d("Price data received, saving in DB.");
    if (event.getResult() != null && event.getResult().getRaw() != null) {
      FullPriceDataEntry dataEntryBtc = event.getResult().getRaw().get(event.getSymbol())
          .get("BTC");
      FullPriceDataEntry dataEntry = event.getResult().getRaw().get(event.getSymbol())
          .get(event.getFiatCurrency());

      CryptoCurrency currency = mCryptoCurrencyDao.loadFullCurrency(event.getId());
      currency.getPriceInformation().setPriceCurrency(dataEntry.getPrice());
      currency.getPriceInformation().setPriceBtc(dataEntryBtc.getPrice());
      currency.setLow24h(dataEntry.getLow24Hour());
      currency.setHigh24h(dataEntry.getLow24Hour());
      currency.setLastUpdated(new DateTime(dataEntry.getLastUpdate() * 1000L));
      mCryptoCurrencyDao.updateCurrency(currency);
    } else {
      Timber
          .w("Couldn't retrieve full priceInformation globalDataResultContent for currency id %s (Symbol %s)",
              event.getId(),
              event.getSymbol());
    }
  }

  public LiveData<CustomAlert> fetchAsyncAlert(long alertId) {
    return mCustomAlertDao.fetchAsyncAlert(alertId);
  }
}
