package com.neoscaler.cryptotrends.application.ui.currencylist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyListEntry;
import com.neoscaler.cryptotrends.application.ui.currencydetail.CurrencyDetailActivity;
import com.neoscaler.cryptotrends.common.ColorCalculator;
import com.neoscaler.cryptotrends.common.CurrencyDataFormatter;
import com.neoscaler.cryptotrends.common.CurrencyIconResolver;
import com.neoscaler.cryptotrends.common.FiatCurrencyConfiguration;
import com.neoscaler.cryptotrends.common.PriceFormatter;
import com.neoscaler.cryptotrends.common.event.ToggleCurrencyUserStatusEvent;
import de.mateware.snacky.Snacky;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

public class CurrencyListAdapter extends
    BaseQuickAdapter<CurrencyListEntry, BaseViewHolder> implements Filterable {

  private static DecimalFormat dfSatoshi = (DecimalFormat) DecimalFormat.getInstance();
  private NumberFormat percentageFormatter = NumberFormat.getInstance();
  private Activity activity;
  private CurrencyListViewModel currencyListViewModel;
  private boolean isFiltering;
  private String filterString;

  CurrencyListAdapter(int layoutResId, @Nullable List<CurrencyListEntry> data,
      Activity activity, CurrencyListViewModel model) {
    super(layoutResId, data);
    this.activity = activity;
    this.currencyListViewModel = model;
    dfSatoshi.setMinimumFractionDigits(8);
    dfSatoshi.setMaximumFractionDigits(8);
    percentageFormatter.setMaximumFractionDigits(2);
  }

  @Override
  public void replaceData(@NonNull Collection<? extends CurrencyListEntry> data) {
    super.replaceData(filterCurrencyList());
  }

  @Override
  protected void convert(BaseViewHolder helper, CurrencyListEntry item) {
    String currencySymbol = FiatCurrencyConfiguration.currencySymbolMap.get(item.getCurrency());

    // Set globalDataResultContent
    // TODO Only for portrait
    String currencyName = CurrencyDataFormatter.formatCurrencyName(activity, item.getName());

    // MarketCap & 24h Volume for large screens or landscape
    String marketCap = PriceFormatter
        .formatPrice(item.getMarketCap(), true, currencySymbol);
    String _24hVolume = PriceFormatter
        .formatPrice(item.getVolume24h(), true, currencySymbol);

    // Prices
    String price = PriceFormatter.formatPrice(item.getPriceCurrency(), true, currencySymbol);
    String priceBtc = PriceFormatter.formatPriceSatoshi(item.getPriceBtc());

    // Changes
    String change1h = percentageFormatter.format(item.getPercentChange1h()) + "%";
    String change24h = percentageFormatter.format(item.getPercentChange24h()) + "%";
    String change7d = percentageFormatter.format(item.getPercentChange7d()) + "%";

    // Set Background colors according to change value
    ColorCalculator.calculateAndSetColors(helper.getView(R.id.currency_list_change_1h),
        item.getPercentChange1h(), item.getLastUpdated());
    ColorCalculator.calculateAndSetColors(helper.getView(R.id.currency_list_change_24h),
        item.getPercentChange24h(), item.getLastUpdated());
    ColorCalculator.calculateAndSetColors(helper.getView(R.id.currency_list_change_7d),
        item.getPercentChange7d(), item.getLastUpdated());

    Drawable icon = CurrencyIconResolver.resolveCurrencyIcon(activity, item.getId());

    showMarkersIfNecessary(helper, item);

    helper
        .setText(R.id.currency_list_currency_name, currencyName)
        .setText(R.id.currency_list_currency_symbol, item.getSymbol())
        .setText(R.id.currency_list_currency_market_cap, marketCap)
        .setText(R.id.currency_list_24h_volume, _24hVolume)
        .setText(R.id.currency_list_price, price)
        .setText(R.id.currency_list_price_btc, priceBtc)
        .setText(R.id.currency_list_change_1h, change1h)
        .setText(R.id.currency_list_change_24h, change24h)
        .setText(R.id.currency_list_change_7d, change7d)
        .setImageDrawable(R.id.currency_list_currency_icon, icon);

    // TODO Until better solution for BRVAH / EasySwipeMenu is found
    initClickListener(helper, item);

    initToggleFavoriteButton(helper, item);
    initToggleWatchlistButton(helper, item);
  }

  private void initClickListener(BaseViewHolder helper, CurrencyListEntry item) {
    helper.getView(R.id.currency_list_line_card_view).setOnClickListener((v -> {
      Intent intent = new Intent(activity, CurrencyDetailActivity.class);

      intent.putExtra("currencyId", item.getId());
      intent.putExtra("currencySymbol", item.getSymbol());
      intent.putExtra("currencyName", item.getName());
      intent.putExtra("currencyFavorite", item.isFavorite());
      intent.putExtra("currencyWatched", item.isWatched());
      activity.startActivity(intent);
    }));
  }

  private void showMarkersIfNecessary(BaseViewHolder helper, CurrencyListEntry item) {
    if (item.isFavorite()) {
      helper.getView(R.id.currency_list_favorite_marker).setVisibility(View.VISIBLE);
    } else {
      helper.getView(R.id.currency_list_favorite_marker).setVisibility(View.GONE);
    }

    if (item.isWatched()) {
      helper.getView(R.id.currency_list_watchlist_marker).setVisibility(View.VISIBLE);
    } else {
      helper.getView(R.id.currency_list_watchlist_marker).setVisibility(View.GONE);
    }
  }

  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence charSequence) {
        String charString = charSequence.toString();

        if (charString.isEmpty()) {
          Timber.d("Back from filtering?");
          isFiltering = false;
        } else {
          isFiltering = true;
          filterString = charString;
        }

        FilterResults filterResults = new FilterResults();
        filterResults.values = filterCurrencyList();
        return filterResults;
      }

      @Override
      protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        currencyListViewModel
            .setPersistedCurrenciesFiltered((List<CurrencyListEntry>) filterResults.values);
      }
    };
  }

  @Override
  public void setNewData(@Nullable List<CurrencyListEntry> data) {
    super.setNewData(filterCurrencyList());
  }

  public void rebuildList() {
    List<CurrencyListEntry> list = filterCurrencyList();
    currencyListViewModel.setPersistedCurrenciesFiltered(list);
  }

  @NonNull
    /*
    Filters the current displayed list according to favorite view mode and/or search box.
     */
  private List<CurrencyListEntry> filterCurrencyList() {
    List<CurrencyListEntry> viewableList = new ArrayList<>();
    List<CurrencyListEntry> favoriteList = new ArrayList<>();

    if (isFiltering) {
      for (CurrencyListEntry currencyEntry : currencyListViewModel.getPersistedCurrencies()
          .getValue()) {
        if (currencyEntry.getSymbol().toLowerCase().contains(filterString)
            || currencyEntry.getName().toLowerCase().contains(filterString)) {
          viewableList.add(currencyEntry);
        }
      }
    } else {
      viewableList = currencyListViewModel.getPersistedCurrencies().getValue();
    }

    if (currencyListViewModel.isFavoriteOnly() && viewableList != null) {
      for (CurrencyListEntry currencyEntry : viewableList) {
        if (currencyEntry.isFavorite()) {
          favoriteList.add(currencyEntry);
        }
      }
      viewableList = favoriteList;
    }

    if (currencyListViewModel.isWatchlistOnly() && viewableList != null) {
      for (CurrencyListEntry currencyEntry : viewableList) {
        if (currencyEntry.isWatched()) {
          favoriteList.add(currencyEntry);
        }
      }
      viewableList = favoriteList;
    }
    return viewableList;
  }

  private void initToggleFavoriteButton(BaseViewHolder helper, CurrencyListEntry item) {
    FancyButton favoriteButton = helper.getView(R.id.currency_line_btn_togglefavorite);
    if (item.isFavorite()) {
      int favoriteIconId = activity.getResources().getIdentifier("ic_star_24dp", "drawable",
          activity.getPackageName());
      Drawable favoriteIcon = activity.getResources().getDrawable(favoriteIconId);
      favoriteButton.setIconResource(favoriteIcon);
    } else {
      int favoriteIconId = activity.getResources()
          .getIdentifier("ic_star_border_white_24dp", "drawable",
              activity.getPackageName());
      Drawable favoriteIcon = activity.getResources().getDrawable(favoriteIconId);
      favoriteButton.setIconResource(favoriteIcon);
    }

    helper.getView(R.id.currency_line_btn_togglefavorite).setOnClickListener(v -> {
      Snacky.builder()
          .setActivity(activity)
          .setView(activity.findViewById(R.id.currencylist_bottom_navigation))
          .setBackgroundColor(activity.getResources().getColor(R.color.primaryDarkColor))
          .setTextColor(activity.getResources().getColor(R.color.snackTextColor))
          .setIcon(
              item.isWatched() ? R.drawable.ic_star_border_white_24dp : R.drawable.ic_star_24dp)
          .setText(
              String.format(activity.getString(R.string.snack_currency_favored), item.getName()))
          .build().show();
      EventBus.getDefault().post(new ToggleCurrencyUserStatusEvent(item.getId(),
          ToggleCurrencyUserStatusEvent.StatusType.FAVORITE));
    });
  }

  private void initToggleWatchlistButton(BaseViewHolder helper, CurrencyListEntry item) {
    FancyButton watchedButton = helper.getView(R.id.currency_line_btn_togglewatchlist);
    if (item.isWatched()) {
      int watchedIconId = activity.getResources().getIdentifier("ic_eye_white_24dp", "drawable",
          activity.getPackageName());
      Drawable watchedIcon = activity.getResources().getDrawable(watchedIconId);
      watchedButton.setIconResource(watchedIcon);
    } else {
      int watchedIconId = activity.getResources().getIdentifier("ic_eye_off", "drawable",
          activity.getPackageName());
      Drawable watchedIcon = activity.getResources().getDrawable(watchedIconId);
      watchedButton.setIconResource(watchedIcon);
    }

    helper.getView(R.id.currency_line_btn_togglewatchlist).setOnClickListener(v -> {
      Snacky.builder()
          .setActivity(activity)
          .setView(activity.findViewById(R.id.currencylist_bottom_navigation))
          .setBackgroundColor(activity.getResources().getColor(R.color.primaryDarkColor))
          .setTextColor(activity.getResources().getColor(R.color.snackTextColor))
          .setIcon(item.isWatched() ? R.drawable.ic_eye_off : R.drawable.ic_eye_white_24dp)
          .setText(
              String.format(activity.getString(R.string.snack_currency_watched), item.getName()))
          .build().show();
      EventBus.getDefault().post(new ToggleCurrencyUserStatusEvent(item.getId(),
          ToggleCurrencyUserStatusEvent.StatusType.WATCHLIST));
    });
  }
}
