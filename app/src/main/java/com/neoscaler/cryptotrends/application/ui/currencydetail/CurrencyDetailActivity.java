package com.neoscaler.cryptotrends.application.ui.currencydetail;

import static com.neoscaler.cryptotrends.common.IntentExtraConstants.EDITADD_ALERT_CURRENCYID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.db.chart.animation.Animation;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.util.Tools;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.LineChartView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.CryptoTrendsApplication;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyListEntry;
import com.neoscaler.cryptotrends.application.network.api.cc.HistoEntry;
import com.neoscaler.cryptotrends.application.ui.MainActivity;
import com.neoscaler.cryptotrends.application.ui.alert.EditAddAlertActivity;
import com.neoscaler.cryptotrends.common.ColorCalculator;
import com.neoscaler.cryptotrends.common.CurrencyIconResolver;
import com.neoscaler.cryptotrends.common.FiatCurrencyConfiguration;
import com.neoscaler.cryptotrends.common.IntentExtraConstants;
import com.neoscaler.cryptotrends.common.PriceFormatter;
import com.neoscaler.cryptotrends.common.event.RemoteProblemDetailEvent;
import com.neoscaler.cryptotrends.common.event.ToggleCurrencyUserStatusEvent;
import de.mateware.snacky.Snacky;
import java.text.NumberFormat;
import java.util.List;
import mehdi.sakout.fancybuttons.FancyButton;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import timber.log.Timber;

public class CurrencyDetailActivity extends AppCompatActivity {

  CurrencyDetailViewModel mCurrencyDetailViewModel;

  @BindView(R.id.tvChange1h)
  TextView textViewChange1h;

  @BindView(R.id.tvChange24h)
  TextView textViewChange24h;

  @BindView(R.id.tvChange7d)
  TextView textViewChange7d;

  @BindView(R.id.textViewCurrentPrice)
  TextView textViewCurrentPrice;

  @BindView(R.id.textViewCurrentPriceBtc)
  TextView textViewCurrentPriceBtc;

  @BindView(R.id.textViewLow24h)
  TextView textViewLow24h;

  @BindView(R.id.textViewHigh24h)
  TextView textViewHigh24h;

  @BindView(R.id.textViewMarketCap)
  TextView textViewMarketCap;

  @BindView(R.id.textViewVolume)
  TextView textView24hvolume;

  @BindView(R.id.textViewSupply)
  TextView textViewSupply;

  @BindView(R.id.barchart)
  HorizontalBarChartView highlowbar;

  @BindView(R.id.linechart)
  LineChartView lineChartView;

  @BindView(R.id.imageViewCurrencyLogo)
  ImageView currencyLogo;

  @BindView(R.id.currency_detail_refreshlayout)
  SwipeRefreshLayout mSwipeRefreshLayout;

  @BindView(R.id.btn_diagtimespan_1h)
  FancyButton timespanButtonH;

  @BindView(R.id.btn_diagtimespan_1d)
  FancyButton timespanButtonD;

  @BindView(R.id.btn_diagtimespan_1w)
  FancyButton timespanButtonW;

  @BindView(R.id.btn_diagtimespan_1m)
  FancyButton timespanButtonM;

  @BindView(R.id.btn_diagtimespan_3m)
  FancyButton timespanButton3M;

  @BindView(R.id.btn_diagtimespan_6m)
  FancyButton timespanButton6M;

  @BindView(R.id.btn_diagtimespan_1y)
  FancyButton timespanButtonY;

  Activity activity;

  DateTimeFormatter datetimeFormatterDate;

  DateTimeFormatter datetimeFormatterTime;

  String currencyId;

  String currencyIdShort;

  String currencyName;

  String currencySymbol;

  boolean favorite;

  boolean watched;

  private FirebaseAnalytics mFirebaseAnalytics;

  private NumberFormat percentageFormatter = NumberFormat.getInstance();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    activity = this;
    super.onCreate(savedInstanceState);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    mFirebaseAnalytics
        .setCurrentScreen(this, getClass().getSimpleName(), getClass().getSimpleName());
    currencyName = getIntent().getStringExtra("currencyName");
    currencyId = getIntent().getStringExtra("currencyId");
    // CC has not symbol in ID
    String[] idWithoutSymbolSplit = currencyId.split("-", 2);
    currencyIdShort = idWithoutSymbolSplit[1];
    currencySymbol = getIntent().getStringExtra("currencySymbol");
    watched = getIntent().getBooleanExtra("currencyWatched", false);
    favorite = getIntent().getBooleanExtra("currencyFavorite", false);
    percentageFormatter.setMaximumFractionDigits(2);

    setContentView(R.layout.activity_currency_detail);
    setTitle(currencyName);

    ButterKnife.bind(this);

    EventBus.getDefault().register(this);

    datetimeFormatterDate = DateTimeFormat.shortDate()
        .withLocale(getResources().getConfiguration().locale);
    datetimeFormatterTime = DateTimeFormat.shortTime()
        .withLocale(getResources().getConfiguration().locale);

    initCurrencyLogo(currencyId);
    initToolbar();
    initViewModel(currencyId, currencySymbol);
    initLineChart();
    highlowbar.setBarBackgroundColor(Color.LTGRAY);
    initLiveDataObserver();
    initPullDownRefresh();
    initDiagramButtons();
  }

  private void initDiagramButtons() {
    timespanButtonH.setOnClickListener(
        v -> mCurrencyDetailViewModel.reloadDiagramValues(DiagramTimespanConfiguration.HOUR));
    timespanButtonD.setOnClickListener(
        v -> mCurrencyDetailViewModel.reloadDiagramValues(DiagramTimespanConfiguration.DAY));
    timespanButtonW.setOnClickListener(
        v -> mCurrencyDetailViewModel.reloadDiagramValues(DiagramTimespanConfiguration.WEEK));
    timespanButtonM.setOnClickListener(
        v -> mCurrencyDetailViewModel.reloadDiagramValues(DiagramTimespanConfiguration.MONTH));
    timespanButton3M.setOnClickListener(
        v -> mCurrencyDetailViewModel.reloadDiagramValues(DiagramTimespanConfiguration.MONTH_3));
    timespanButton6M.setOnClickListener(
        v -> mCurrencyDetailViewModel.reloadDiagramValues(DiagramTimespanConfiguration.MONTH_6));
    timespanButtonY.setOnClickListener(
        v -> mCurrencyDetailViewModel.reloadDiagramValues(DiagramTimespanConfiguration.YEAR));
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageRemoteProblem(RemoteProblemDetailEvent event) {
    Snacky.builder()
        .setActivity(this)
        .setTextColor(getResources().getColor(R.color.snackTextColor))
        .error()
        .setText(com.neoscaler.cryptotrends.R.string.currencylist_remoteexception)
        .show();
    Timber.e(event.getThrowable(), event.getMessage());
  }

  private void initLiveDataObserver() {
    mCurrencyDetailViewModel.getCurrencyEntry().observe(this,
        newItem -> {
          if (newItem != null) {
            showTextValues(newItem);
            setupHighLowBar(newItem);
            showChangeValues(newItem);
          }
        });
  }

  private void initToolbar() {
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  private void initCurrencyLogo(String currencyId) {
    Drawable icon = CurrencyIconResolver.resolveCurrencyIcon(this, currencyId);
    if (icon != null) {
      currencyLogo.setImageDrawable(icon);
    } else {
      currencyLogo.setImageDrawable(null);
      currencyLogo.setVisibility(View.INVISIBLE);
    }
  }

  private void initViewModel(String currencyId, String currencySymbol) {
    CurrencyDetailViewModel.Factory factory = new CurrencyDetailViewModel.Factory(
        ((CryptoTrendsApplication) this.getApplication()).getDataRepository(), currencyId,
        currencySymbol);
    mCurrencyDetailViewModel = ViewModelProviders.of(this, factory)
        .get(CurrencyDetailViewModel.class);
  }

  private void showTextValues(CurrencyListEntry newItem) {
    String price = PriceFormatter.formatPrice(newItem.getPriceCurrency(), true,
        FiatCurrencyConfiguration.currencySymbolMap.get(newItem.getCurrency()));
    textViewCurrentPrice.setText(price);
    textViewCurrentPriceBtc.setText(PriceFormatter.formatPriceSatoshi(newItem.getPriceBtc()));

    String marketCap = PriceFormatter.formatPrice(newItem.getMarketCap(), true,
        FiatCurrencyConfiguration.currencySymbolMap.get(newItem.getCurrency()));
    String _24hVolume = PriceFormatter.formatPrice(newItem.getVolume24h(), true,
        FiatCurrencyConfiguration.currencySymbolMap.get(newItem.getCurrency()));
    String supply = NumberFormat.getInstance().format(newItem.getCirculatingSupply());
    textViewMarketCap.setText(marketCap);
    textView24hvolume.setText(_24hVolume);
    textViewSupply.setText(supply);
  }

  private void showChangeValues(CurrencyListEntry newItem) {
    ColorCalculator.calculateAndSetColors(textViewChange1h, newItem.getPercentChange1h(),
        newItem.getLastUpdated());
    ColorCalculator.calculateAndSetColors(textViewChange24h, newItem.getPercentChange24h(),
        newItem.getLastUpdated());
    ColorCalculator.calculateAndSetColors(textViewChange7d, newItem.getPercentChange7d(),
        newItem.getLastUpdated());
    textViewChange1h.setText(percentageFormatter.format(newItem.getPercentChange1h()) + "%");
    textViewChange24h.setText(percentageFormatter.format(newItem.getPercentChange24h()) + "%");
    textViewChange7d.setText(percentageFormatter.format(newItem.getPercentChange7d()) + "%");
  }

  private void setupHighLowBar(CurrencyListEntry newItem) {
    if (newItem.getLow24h() != 0L && newItem.getHigh24h() != 0L) {
      try {
        textViewHigh24h.setVisibility(View.VISIBLE);
        textViewLow24h.setVisibility(View.VISIBLE);
        highlowbar.setVisibility(View.VISIBLE);

        textViewLow24h.setText("L " + NumberFormat.getInstance().format(newItem.getLow24h()));
        textViewHigh24h.setText("H " + NumberFormat.getInstance().format(newItem.getHigh24h()));

        BarSet set = new BarSet();
        Bar bar = new Bar("", (float) newItem.getPriceCurrency());
        bar.setColor(getResources().getColor(R.color.primaryDarkColor));
        set.addBar(bar);

        highlowbar.reset();
        highlowbar.setXAxis(false);
        highlowbar.setYAxis(false);
        highlowbar.setXLabels(AxisRenderer.LabelPosition.NONE);
        // Fix for issue #9
        if (newItem.getHigh24h() > newItem.getLow24h()) {
          highlowbar.setAxisBorderValues((float) newItem.getLow24h(), (float) newItem.getHigh24h());
        }
        highlowbar.addData(set);
        highlowbar.show(new Animation().inSequence(.5f));
      } catch (Throwable e) {
        Timber.e(e, "Error building bar chart, disabling it.");
        textViewHigh24h.setVisibility(View.GONE);
        textViewLow24h.setVisibility(View.GONE);
        highlowbar.setVisibility(View.GONE);
      }
    } else {
      textViewHigh24h.setVisibility(View.GONE);
      textViewLow24h.setVisibility(View.GONE);
      highlowbar.setVisibility(View.GONE);
    }
  }

  private void initLineChart() {
    // Observer for LiveData
    mCurrencyDetailViewModel.getDiagramData().observe(this, diagramTimespanResult -> {
      // Convert to chart data
          List<HistoEntry> entries = diagramTimespanResult.getHistoResult().getData();
          boolean intraDayResult = diagramTimespanResult.getConfig() == DiagramTimespanConfiguration.DAY
              || diagramTimespanResult.getConfig() == DiagramTimespanConfiguration.HOUR;

          if (entries != null && entries.size() != 0) {
            // TODO Refactor without array shit
            int labelCount = 5;
            int labelFrequency = entries.size() / labelCount;
            float[] mValues = new float[entries.size()];
            String[] mLabels = new String[entries.size()];
            int i = 0;
            float max = 0;
            float min = 0;
            for (HistoEntry entry : entries) {
              mValues[i] = entry.getClose().floatValue();

              if (i == 0) {
                max = mValues[i];
                min = mValues[i];
              } else {
                max = mValues[i] > max ? mValues[i] : max;
                min = mValues[i] < min ? mValues[i] : min;
              }

              if (i % labelFrequency == 0) {
                DateTime dateTime = new DateTime(entry.getTime() * 1000L);
                mLabels[i] = intraDayResult ? datetimeFormatterTime.print(dateTime)
                    : datetimeFormatterDate.print(dateTime);
              } else {
                mLabels[i] = "";
              }
              i++;
            }
            LineSet dataset = new LineSet(mLabels, mValues);
            dataset.setColor(getResources().getColor(R.color.primaryDarkColor))
                .setThickness(Tools.fromDpToPx(3))
                .setSmooth(true)
                .setFill(getResources().getColor(R.color.primaryDarkColor))
                .setGradientFill(new int[]{Color.parseColor("#00766c"),
                        getResources().getColor(R.color.primaryLightColor)},
                    null);

            // Refresh LineChart
            lineChartView.reset();
            if (!diagramTimespanResult.getConfig().isShowYAxisFromZero()) {
              lineChartView.setAxisBorderValues(0 < min - (min * 0.02F) ? min - (min * 0.02F) : min,
                  max + (max * 0.02F));
            }
            lineChartView.addData(dataset);
            lineChartView.setAxisColor(getResources().getColor(R.color.primaryTextColor));
            lineChartView.setLabelsColor(getResources().getColor(R.color.primaryTextColor));
            lineChartView.show();
          }
        }

    );
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_currency_detail, menu);

    MenuItem toggleFavorite = menu.findItem(R.id.action_togglefavorite);
    toggleFavorite.setIcon(favorite ?
        R.drawable.ic_star_24dp : R.drawable.ic_star_border_white_24dp);
    MenuItem toggleWatchlist = menu.findItem(R.id.action_togglewatchlist);
    toggleWatchlist.setIcon(watched ?
        R.drawable.ic_eye_white_24dp : R.drawable.ic_eye_off);

    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case (R.id.action_togglefavorite):
        favorite = !favorite;
        item.setIcon(favorite ?
            R.drawable.ic_star_24dp : R.drawable.ic_star_border_white_24dp);

        Snacky.builder().setActivity(activity)
            .setBackgroundColor(getResources().getColor(R.color.primaryDarkColor))
            .setTextColor(getResources().getColor(R.color.snackTextColor))
            .setIcon(favorite ?
                R.drawable.ic_star_24dp : R.drawable.ic_star_border_white_24dp)
            .setText(
                String.format(activity.getString(R.string.snack_currency_favored), currencyName))
            .build().show();
        EventBus.getDefault().post(new ToggleCurrencyUserStatusEvent(currencyId,
            ToggleCurrencyUserStatusEvent.StatusType.FAVORITE));
        return true;
      case (R.id.action_togglewatchlist):
        watched = !watched;
        item.setIcon(watched ?
            R.drawable.ic_eye_white_24dp : R.drawable.ic_eye_off);

        Snacky.builder().setActivity(activity)
            .setBackgroundColor(getResources().getColor(R.color.primaryDarkColor))
            .setTextColor(getResources().getColor(R.color.snackTextColor))
            .setIcon(watched ?
                R.drawable.ic_eye_white_24dp : R.drawable.ic_eye_off)
            .setText(
                String.format(activity.getString(R.string.snack_currency_watched), currencyName))
            .build().show();
        EventBus.getDefault().post(new ToggleCurrencyUserStatusEvent(currencyId,
            ToggleCurrencyUserStatusEvent.StatusType.WATCHLIST));
        return true;
      case (R.id.action_addalert):
        Intent intent = new Intent(this, EditAddAlertActivity.class);
        intent.putExtra(EDITADD_ALERT_CURRENCYID, currencyId);
        startActivityForResult(intent, IntentExtraConstants.CREATE_ALERT_REQUEST);
        return true;
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    switch (requestCode) {
      case IntentExtraConstants.CREATE_ALERT_REQUEST:
        if (resultCode == EditAddAlertActivity.ALERT_CREATED_RESULT) {
          Snacky.builder().setActivity(this)
              .setBackgroundColor(getResources().getColor(R.color.primaryDarkColor))
              .setTextColor(getResources().getColor(R.color.snackTextColor))
              .setIcon(R.drawable.ic_check_white_24dp)
              .setText(com.neoscaler.cryptotrends.R.string.addeditalert_alert_created)
              .setDuration(Snacky.LENGTH_LONG)
              .setActionText(com.neoscaler.cryptotrends.R.string.currencydetail_showcreatedalert)
              .setActionTextColor(getResources().getColor(R.color.secondaryLightColor))
              .setActionClickListener(v1 -> {
                Intent newIntent = new Intent(this, MainActivity.class);
                newIntent.putExtra(IntentExtraConstants.FRAGMENTLOAD_PARAMETER,
                    IntentExtraConstants.FRAGMENT_ALERTLIST);
                startActivity(newIntent);
              })
              .build()
              .show();
        }
    }
  }

  private void initPullDownRefresh() {
    mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
      mCurrencyDetailViewModel.refreshCurrencyDetailValues();
      mSwipeRefreshLayout.setRefreshing(false);
    }, 1000));
  }

}
