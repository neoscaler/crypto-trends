package com.neoscaler.cryptotrends.application.ui.alert;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.projection.CustomAlertEntry;
import com.neoscaler.cryptotrends.common.CurrencyIconResolver;
import com.neoscaler.cryptotrends.common.FiatCurrencyConfiguration;
import com.neoscaler.cryptotrends.common.IntentExtraConstants;
import com.neoscaler.cryptotrends.common.PriceFormatter;
import com.neoscaler.cryptotrends.common.SignalTypeIconResolver;
import com.neoscaler.cryptotrends.common.event.DeleteAlertEvent;
import com.neoscaler.cryptotrends.common.event.ToggleStatusAlertEvent;
import java.text.DecimalFormat;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AlertListAdapter extends BaseSectionQuickAdapter<CustomAlertEntry, BaseViewHolder> {

  Activity activity;

  DecimalFormat dfPercentage = (DecimalFormat) DecimalFormat.getInstance();

  DateTimeFormatter dateTimeFormatter;

  public AlertListAdapter(int layoutResId, int sectionHeadResId,
      @Nullable List<CustomAlertEntry> data, Activity activity) {
    super(layoutResId, sectionHeadResId, data);
    this.activity = activity;
    dfPercentage.setMaximumFractionDigits(1);
    dfPercentage.setMinimumFractionDigits(0);
    dfPercentage.setPositivePrefix("+");
    dfPercentage.setNegativePrefix("-");

    dateTimeFormatter = DateTimeFormat.shortDate()
        .withLocale(activity.getResources().getConfiguration().locale);
  }

  @Override
  protected void convert(BaseViewHolder helper, CustomAlertEntry item) {
    Drawable icon = CurrencyIconResolver.resolveCurrencyIcon(activity, item.getCurrencyId());

    // Prices & Percentages
    String baseCurrencySymbol = FiatCurrencyConfiguration.currencySymbolMap
        .get(item.getBaseCurrency());
    String priceBase = PriceFormatter.formatPrice(item.getPriceBase(), true, baseCurrencySymbol);
    String priceTarget = PriceFormatter
        .formatPrice(item.getPriceThresholdBaseCurrency(), true, baseCurrencySymbol);
    String priceNow = PriceFormatter
        .formatPrice(item.getPriceLastChecked(), true, baseCurrencySymbol);

    double percentageTarget =
        (item.getPriceThresholdBaseCurrency() - item.getPriceBase()) / item.getPriceBase() * 100;
    double percentageNow =
        (item.getPriceLastChecked() - item.getPriceBase()) / item.getPriceBase() * 100;
    String percentageTargetString = PriceFormatter.formatPercentage((long) percentageTarget);
    String percentageNowString = PriceFormatter.formatPercentage((long) percentageNow);
    if (percentageTarget > 0) {
      ((TextView) helper.getView(R.id.alert_line_pricethreshold_percentage))
          .setTextColor(activity.getResources().getColor(R.color.primaryDarkColor));
    } else {
      ((TextView) helper.getView(R.id.alert_line_pricethreshold_percentage))
          .setTextColor(activity.getResources().getColor(R.color.secondaryDarkColor));
    }

    // Dates
    String createdAt = dateTimeFormatter.print(item.getCreatedAt());

    // SwipeMenu
    initDeleteButton(helper, item);
    initToggleStatusButton(helper, item);

    initClickListener(helper, item);

    showCurrentPrice(helper, item, priceNow, percentageNowString, percentageNow);
    showActiveStatus(helper, item);

    // Set view contents
    helper.setText(R.id.alert_line_currency_name, item.getCurrencyName())
        .setText(R.id.alert_line_currency_symbol, item.getCurrencySymbol())
        .setText(R.id.alert_line_pricebase, priceBase)
        .setText(R.id.alert_line_pricethreshold, priceTarget)
        .setText(R.id.alert_line_pricethreshold_percentage, percentageTargetString)
        .setText(R.id.alert_line_createdAt, createdAt)
        .setImageDrawable(R.id.alert_line_logo, icon);
  }

  private void showActiveStatus(BaseViewHolder helper, CustomAlertEntry item) {
    if (!item.isActive()) {
      helper.getView(R.id.background_foreground_greyout).setVisibility(View.VISIBLE);
    } else {
      helper.getView(R.id.background_foreground_greyout).setVisibility(View.INVISIBLE);
    }
  }

  private void showCurrentPrice(BaseViewHolder helper, CustomAlertEntry item, String priceNow,
      String percentageNowString, double percentageNow) {
    if (item.getPriceLastChecked() != 0) {
      helper.getView(R.id.alert_line_pricenow).setVisibility(View.VISIBLE);
      helper.getView(R.id.alert_line_pricenow_percentage).setVisibility(View.VISIBLE);
      helper.setText(R.id.alert_line_pricenow, priceNow)
          .setText(R.id.alert_line_pricenow_percentage, percentageNowString);
      if (percentageNow > 0) {
        ((TextView) helper.getView(R.id.alert_line_pricenow_percentage))
            .setTextColor(Color.parseColor("#26a69a"));
      } else {
        ((TextView) helper.getView(R.id.alert_line_pricenow_percentage))
            .setTextColor(Color.parseColor("#ac241a"));
      }
    } else {
      // Price not yet checked
      helper.getView(R.id.alert_line_pricenow).setVisibility(View.INVISIBLE);
      helper.getView(R.id.alert_line_pricenow_percentage).setVisibility(View.INVISIBLE);
    }
  }

  private void initDeleteButton(BaseViewHolder helper, CustomAlertEntry item) {
    helper.getView(R.id.alert_line_btn_delete).setOnClickListener(v -> {
      DeleteAlertEvent event = new DeleteAlertEvent(item.getId());
      EventBus.getDefault().post(event);
    });
  }

  private void initToggleStatusButton(BaseViewHolder helper, CustomAlertEntry item) {
    helper.getView(R.id.alert_line_btn_activetoggle).setOnClickListener(v -> {
      ToggleStatusAlertEvent event = new ToggleStatusAlertEvent(item.getId());
      EventBus.getDefault().post(event);
    });
  }


  @Override
  protected void convertHead(BaseViewHolder helper, CustomAlertEntry item) {
    Drawable signalTypeIcon = SignalTypeIconResolver
        .resolveSignalTypeIcon(activity, item.getSignalType());
    String headerText;
    switch (item.getSignalType()) {
      case BUY:
        headerText = activity.getString(R.string.addeditalert_signaltype_subheader_buy);
        break;
      case SELL:
        headerText = activity.getString(R.string.addeditalert_signaltype_subheader_sell);
        break;
      default:
        headerText = activity.getString(R.string.addeditalert_signaltype_subheader_none);
    }

    helper
        .setText(R.id.signalTypeSubheaderLabel, headerText)
        .setImageDrawable(R.id.signalTypeSubheaderIcon, signalTypeIcon);
  }

  private void initClickListener(BaseViewHolder helper, CustomAlertEntry item) {
    helper.getView(R.id.alert_list_line_card_view).setOnClickListener((v -> {
      Intent intent = new Intent(activity, EditAddAlertActivity.class);
      intent.putExtra(IntentExtraConstants.EDITADD_ALERT_ALERTID, item.getId());
      activity.startActivity(intent);
    }));
  }
}

