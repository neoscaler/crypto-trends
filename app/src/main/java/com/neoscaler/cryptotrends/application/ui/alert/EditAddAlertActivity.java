package com.neoscaler.cryptotrends.application.ui.alert;

import static com.neoscaler.cryptotrends.common.SharedPrefsKeys.SETTINGS_GENERAL_DISPLAYCURRENCY;

import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.CryptoTrendsApplication;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import com.neoscaler.cryptotrends.application.model.CustomAlert.SignalType;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyBasic;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import com.neoscaler.cryptotrends.common.FiatCurrencyConfiguration;
import com.neoscaler.cryptotrends.common.IntentExtraConstants;
import com.neoscaler.cryptotrends.common.PriceFormatter;
import com.neoscaler.cryptotrends.common.event.SaveAlertEvent;
import com.pixplicity.easyprefs.library.Prefs;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import de.mateware.snacky.Snacky;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mehdi.sakout.fancybuttons.FancyButton;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import timber.log.Timber;

public class EditAddAlertActivity extends AppCompatActivity {

  public static final int ALERT_CREATED_RESULT = 1;
  @BindView(R.id.btn_save_alert)
  FancyButton saveButton;
  @BindView(R.id.edit_alert_currency_id)
  SearchableSpinner currencySpinner;
  @BindView(R.id.edit_alert_price_threshold)
  MaterialEditText priceThreshold;
  @BindView(R.id.edit_alert_price_base)
  MaterialEditText priceBase;
  @BindView(R.id.edit_alert_price_base_layout)
  TextInputLayout priceBaseLabel;
  @BindView(R.id.edit_alert_price_threshold_layout)
  TextInputLayout priceThresholdLabel;
  @BindView(R.id.edit_alert_signal_type)
  Spinner signalTypeSpinner;
  @BindView(R.id.edit_alert_notes)
  EditText notesText;
  @BindView(R.id.currentPriceInfo)
  TextView currentPriceInfo;
  EditAddAlertViewModel editAddAlertViewModel;
  DataRepository dataRepository;
  NumberFormat numberFormat;
  private FirebaseAnalytics mFirebaseAnalytics;
  private long alertId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    mFirebaseAnalytics
        .setCurrentScreen(this, getClass().getSimpleName(), getClass().getSimpleName());
    setContentView(R.layout.activity_editalert);
    ButterKnife.bind(this);
    setTitle(getString(R.string.addeditalert_title));
    dataRepository = ((CryptoTrendsApplication) getApplicationContext()).getDataRepository();

    initFormatter();
    initCurrencySymbolHints();
    initViewModel();
    initCurrencySpinner();
    initObserverExistingAlert();
    initSaveButton();
  }

  private void initCurrencySymbolHints() {
    String displaySymbol = FiatCurrencyConfiguration.currencySymbolMap.get(
        Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD"));
    priceBaseLabel
        .setHint(String.format(getString(R.string.addeditalert_base_price), displaySymbol));
    priceThresholdLabel
        .setHint(String.format(getString(R.string.addeditalert_threshold_price), displaySymbol));
  }

  private void initFormatter() {
    numberFormat = NumberFormat.getInstance();
    numberFormat.setGroupingUsed(false);
  }

  private void initCurrencySpinner() {
    currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCurrencyId = resolveCurrencyId();
        editAddAlertViewModel
            .setSelectedCurrency(dataRepository.loadFullCurrencyAsync(selectedCurrencyId));
        editAddAlertViewModel.getSelectedCurrency()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(currency -> {
              editAddAlertViewModel.setCachedCurrency(currency);
              currentPriceInfo.setText(Html.fromHtml(String.format(
                  getString(R.string.addeditalert_currentpriceinfo),
                  currency.getSymbol(),
                  PriceFormatter.formatPrice(currency.getPriceInformation().getPriceCurrency(), true,
                      FiatCurrencyConfiguration.currencySymbolMap.get(
                          Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD"))))));
              currentPriceInfo.setVisibility(View.VISIBLE);
            });
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {
        currentPriceInfo.setVisibility(View.INVISIBLE);
      }
    });

    editAddAlertViewModel.getAvailableCurrencies().observe(this, newItems -> {
      Bundle bundle = getIntent().getExtras();
      String preselectedCurrencyId = null;
      if (bundle != null) {
        preselectedCurrencyId = bundle.getString(IntentExtraConstants.EDITADD_ALERT_CURRENCYID);
      }

      List<String> currencyNames = new ArrayList<>();
      String preselectedCurrencyName = null;
      for (CurrencyBasic currencyBasic : newItems) {
        currencyNames.add(currencyBasic.getName());
        if (preselectedCurrencyId != null && currencyBasic.getId().equals(preselectedCurrencyId)) {
          preselectedCurrencyName = currencyBasic.getName();
        }
      }
      ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>
          (this, android.R.layout.simple_spinner_item,
              currencyNames);
      currencySpinner.setAdapter(spinnerArrayAdapter);

      // Preselect if started for specific currency
      if (preselectedCurrencyName != null) {
        int spinnerPosition = spinnerArrayAdapter.getPosition(preselectedCurrencyName);
        currencySpinner.post(() -> currencySpinner.setSelection(spinnerPosition));
      }
    });
    currentPriceInfo.setOnClickListener(v -> {
      priceBase.setText(PriceFormatter
          .formatPrice(editAddAlertViewModel.getCachedCurrency().getPriceInformation().getPriceCurrency(), false,
              null, Locale.US));
    });
  }

  private void initViewModel() {
    Bundle bundle = getIntent().getExtras();
    alertId = -1;
    if (bundle != null) {
      alertId = bundle.getLong(IntentExtraConstants.EDITADD_ALERT_ALERTID, -1);
    }
    EditAddAlertViewModel.Factory factory = new EditAddAlertViewModel.Factory(
        ((CryptoTrendsApplication) this.getApplication()).getDataRepository(), alertId);
    editAddAlertViewModel = ViewModelProviders.of(this, factory).get(EditAddAlertViewModel.class);
  }

  private void initObserverExistingAlert() {
    if (editAddAlertViewModel.getCustomAlert() != null) {
      editAddAlertViewModel.getCustomAlert().observe(this,
          newItem -> {
            if (newItem != null) {
              currencySpinner.post(() -> {
                if (currencySpinner.getAdapter() != null) {
                  currencySpinner.setSelection(((ArrayAdapter<String>) currencySpinner.getAdapter())
                      .getPosition(newItem.getCurrencyName()));
                }
              });
              priceThreshold.setText(PriceFormatter
                  .formatPrice(newItem.getPriceThresholdBaseCurrency(), false, null, Locale.US));
              priceBase.setText(
                  PriceFormatter.formatPrice(newItem.getPriceBase(), false, null, Locale.US));
              if (newItem.getSignalType() == SignalType.BUY) {
                signalTypeSpinner.post(() -> signalTypeSpinner.setSelection(
                    ((ArrayAdapter<String>) signalTypeSpinner.getAdapter()).getPosition("Buy")
                        + 1));
              }
              if (newItem.getSignalType() == SignalType.SELL) {
                signalTypeSpinner.post(() -> signalTypeSpinner.setSelection(
                    ((ArrayAdapter<String>) signalTypeSpinner.getAdapter()).getPosition("Sell")
                        + 1));
              }
              notesText.setText(String.valueOf(newItem.getNotes()));
            }
          });
    }
  }

  private void initSaveButton() {
    saveButton.setOnClickListener(v -> {
      try {
        RegexpValidator validator = new RegexpValidator(
            getString(R.string.addeditalert_no_valid_price), "[0-9]*\\.?[0-9]+");
        // Validation
        boolean valid = true;
        if (resolveCurrencyId() == null) {
          valid = false;
          // TODO Mark with red error in GUI
        }
        if (!priceBase.validateWith(validator)) {
          valid = false;
        }
        if (!priceThreshold.validateWith(validator)) {
          valid = false;
        }

        if (valid) {
          SaveAlertEvent saveAlertEvent = new SaveAlertEvent(
              createNewAlert(
                  editAddAlertViewModel.getCustomAlert() != null
                      ? editAddAlertViewModel.getCustomAlert().getValue()
                      : null));
          EventBus.getDefault().post(saveAlertEvent);
          setResult(ALERT_CREATED_RESULT);
          finish();
        } else {
          Snacky.builder().setActivity(this)
              .error()
              .setText(R.string.addeditalert_input_not_valid)
              .show();
        }
      } catch (Exception e) {
        Timber.e(e);
        // TODO Send error event
        finish();
      }
    });
  }

  @NonNull
  private CustomAlert createNewAlert(CustomAlert existingAlert) {
    CustomAlert customAlert;
    if (existingAlert == null) {
      customAlert = new CustomAlert();
      customAlert.setCreatedAt(DateTime.now());
    } else {
      customAlert = existingAlert;
    }

    String selectedCurrencyId = resolveCurrencyId();
    customAlert.setCurrencyId(selectedCurrencyId);
    customAlert.setPriceBase(Double.parseDouble(priceBase.getText().toString()));
    customAlert.setBaseCurrency(Prefs.getString(SETTINGS_GENERAL_DISPLAYCURRENCY, "USD"));
    customAlert
        .setPriceThresholdBaseCurrency(Double.parseDouble(priceThreshold.getText().toString()));
    createSignalType(customAlert);
    customAlert.setNotes(notesText.getText().toString());

    return customAlert;
  }

  private String resolveCurrencyId() {
    if (currencySpinner.getSelectedItem() == null) {
      return null;
    }
    for (CurrencyBasic currencyBasic : editAddAlertViewModel.getAvailableCurrencies().getValue()) {
      if (currencySpinner.getSelectedItem() != null &&
          currencySpinner.getSelectedItem().toString().equals(currencyBasic.getName())) {
        return currencyBasic.getId();
      }
    }
    return null;
  }

  private void createSignalType(CustomAlert customAlert) {
    if (signalTypeSpinner.getSelectedItem() == null) {
      customAlert.setSignalType(SignalType.NONE);
      return;
    }
    String signalTypeSpinnerText = signalTypeSpinner.getSelectedItem().toString();
    SignalType signalType;
    switch (signalTypeSpinnerText) {
      case "Buy":
        signalType = SignalType.BUY;
        break;
      case "Sell":
        signalType = SignalType.SELL;
        break;
      default:
        signalType = SignalType.NONE;
    }
    customAlert.setSignalType(signalType);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
