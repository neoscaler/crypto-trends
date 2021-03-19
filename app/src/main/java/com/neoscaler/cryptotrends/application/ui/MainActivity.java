package com.neoscaler.cryptotrends.application.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.CryptoTrendsApplication;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.GlobalMarketData;
import com.neoscaler.cryptotrends.application.ui.about.AboutFragment;
import com.neoscaler.cryptotrends.application.ui.alert.AlertListFragment;
import com.neoscaler.cryptotrends.application.ui.currencylist.CurrencyListFragment;
import com.neoscaler.cryptotrends.application.ui.currencylist.CurrencyListViewModel;
import com.neoscaler.cryptotrends.application.ui.settings.SettingsActivity;
import com.neoscaler.cryptotrends.common.FiatCurrencyConfiguration;
import com.neoscaler.cryptotrends.common.IntentExtraConstants;
import com.neoscaler.cryptotrends.common.PriceFormatter;
import com.neoscaler.cryptotrends.common.event.CurrencyListUpdatedEvent;
import com.neoscaler.cryptotrends.common.event.CurrencyListUpdatedStartedEvent;
import com.neoscaler.cryptotrends.common.event.RemoteProblemEvent;
import de.mateware.snacky.Snacky;
import io.github.tonnyl.whatsnew.WhatsNew;
import io.github.tonnyl.whatsnew.item.WhatsNewItem;
import java.text.NumberFormat;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  private static final int TIME_DELAY = 2000;
  private static long back_pressed;
  @BindView(R.id.toolbar)
  Toolbar mToolbar;
  @BindView(R.id.drawer_layout)
  DrawerLayout mDrawer;
  @BindView(R.id.nav_view)
  NavigationView mNavigationView;
  TextView textViewMarketCap;
  TextView textView24hVolume;
  TextView textViewBtcDominance;
  TextView textViewLastUpdated;

  TextView toolbarMarketCap;
  TextView toolbar24hVolume;

  DateTimeFormatter fmt;
  CurrencyListViewModel mCurrencyListViewModel;
  private FirebaseAnalytics mFirebaseAnalytics;

  NumberFormat nf;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    nf = NumberFormat.getInstance();
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);
    setSupportActionBar(mToolbar);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    mDrawer.addDrawerListener(toggle);
    mNavigationView.setNavigationItemSelectedListener(this);
    toggle.syncState();

    fmt = DateTimeFormat.shortDateTime()
        .withLocale(getResources().getConfiguration().locale);
    PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

    initNavDrawerHeader();
    initViewModel();
    initNavigationView(savedInstanceState);
    initFragmentIfNecessary();

    EventBus.getDefault().register(this);

    showWhatsNew();

/*    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads()
        .detectDiskWrites()
        .detectNetwork()
        .penaltyLog()
        //.penaltyDeath()
        .build());*/
  }

  private void showWhatsNew() {
    WhatsNew whatsNew = WhatsNew.newInstance(
        new WhatsNewItem("New fiat base currencies",
            "We now support ~35 fiat currencies instead of just four. Check the settings to see whether your currency is supported.",
            R.drawable.ic_attach_money_black_24dp),
        new WhatsNewItem("All coins available",
            "Support for all currencies/coins is back, not only the top 100!",
            R.drawable.ic_list_black_24dp),
        new WhatsNewItem("API switch",
            "We had to switch our data provider, for what we unfortunately had to reset the local app state. Please add your favorites and alarms again! Sorry for the inconvenience.",
            R.drawable.ic_warning_black_24dp)
    );
    styleWhatsNew(whatsNew);
    whatsNew.presentAutomatically(MainActivity.this);
  }

  private void styleWhatsNew(WhatsNew whatsNew) {
    whatsNew.setTitleColor(ContextCompat.getColor(this, R.color.colorPrimaryBackgroundDark));
    whatsNew.setIconColor(ContextCompat.getColor(this, R.color.colorPrimaryBackgroundDark));

    whatsNew.setItemTitleColor(ContextCompat.getColor(this, R.color.primaryTextColor));
    whatsNew.setItemContentColor(ContextCompat.getColor(this, R.color.secondaryTextColor));

    whatsNew.setButtonBackground(ContextCompat.getColor(this, R.color.colorPrimaryBackgroundDark));
    whatsNew.setButtonTextColor(ContextCompat.getColor(this, R.color.snackTextColor));
  }

  private void initFragmentIfNecessary() {
    // Load specific fragment by calling menu item
    if (getIntent().getExtras() != null &&
        getIntent().getExtras().getString(IntentExtraConstants.FRAGMENTLOAD_PARAMETER) != null) {
      String intentFragment = getIntent().getExtras()
          .getString(IntentExtraConstants.FRAGMENTLOAD_PARAMETER);
      if (intentFragment != null) {
        switch (intentFragment) {
          case IntentExtraConstants.FRAGMENT_ALERTLIST:
            mNavigationView.getMenu().performIdentifierAction(R.id.nav_alerts, 0);
            break;
          default:
            throw new IllegalArgumentException("Unknown fragment to select.");
        }
      }
    }
  }

  private void initNavigationView(Bundle savedInstanceState) {
    // Load initial fragment
    if (savedInstanceState == null) {
      mNavigationView.getMenu().performIdentifierAction(R.id.nav_market, 0);
    }
  }

  private void initViewModel() {
    CurrencyListViewModel.Factory factory = new CurrencyListViewModel.Factory(
        ((CryptoTrendsApplication) this.getApplication()).getDataRepository());
    mCurrencyListViewModel = ViewModelProviders.of(this, factory).get(CurrencyListViewModel.class);
    mCurrencyListViewModel.getGlobalMarketData().observe(this,
        this::showNewGlobalMarketData);
  }

  private void initNavDrawerHeader() {
    // Workaround for https://issuetracker.google.com/issues/37066763
    View header = mNavigationView.getHeaderView(0);
    textViewMarketCap = header.findViewById(R.id.textViewMarketCap);
    textView24hVolume = header.findViewById(R.id.textView24hVolume);
    textViewBtcDominance = header.findViewById(R.id.textViewBtcDominance);
    textViewLastUpdated = header.findViewById(R.id.textViewLastUpdated);
    toolbar24hVolume = findViewById(R.id.toolbar24hVolume);
    toolbarMarketCap = findViewById(R.id.toolbarMarketCap);
  }

  // Called by livedata observer when globalDataResultContent changes
  private void showNewGlobalMarketData(GlobalMarketData globalMarketData) {
    if (globalMarketData != null) {
      String currencySymbol = FiatCurrencyConfiguration.currencySymbolMap
          .get(globalMarketData.getCurrency());

      double capB = globalMarketData.getTotalMarketCap() / 1000000000D;
      double volB = globalMarketData.getTotal24hVolume() / 1000000000D;

      String capBformatted = "US" + PriceFormatter
          .formatPrice(capB, true, currencySymbol);
      String volMformatted = "US" + PriceFormatter
          .formatPrice(volB, true, currencySymbol);
      String lastUpdated = fmt.print(globalMarketData.getLastUpdated());

      // Nav Drawer
      textViewMarketCap.setText(capBformatted + " B");
      textView24hVolume.setText(volMformatted + " B");
      textViewBtcDominance
          .setText(String.format("%.2f %%", globalMarketData.getMarketCapPercentageBitcoin()));
      // TODO: Set and show EthPercentage
      textViewLastUpdated.setText(lastUpdated);

      // Toolbar
      toolbar24hVolume = findViewById(R.id.toolbar24hVolume);
      toolbarMarketCap = findViewById(R.id.toolbarMarketCap);
      if (toolbarMarketCap != null && toolbar24hVolume != null) {
        toolbarMarketCap
            .setText(String.format(getString(R.string.toolbar_billion_suffix), capBformatted));
        toolbar24hVolume
            .setText(String.format(getString(R.string.toolbar_billion_suffix), volMformatted));
      }
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEventUpdated(CurrencyListUpdatedEvent event) {
    Snacky.builder()
        .setView(findViewById(R.id.currencylist_coordinator))
        .setBackgroundColor(getResources().getColor(R.color.primaryDarkColor))
        .setTextColor(getResources().getColor(R.color.snackTextColor))
        .setIcon(R.drawable.ic_check_white_24dp)
        .setText(com.neoscaler.cryptotrends.R.string.currencylist_pricedataupdated)
        .setDuration(Snacky.LENGTH_SHORT)
        .build()
        .show();
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageRemoteProblem(RemoteProblemEvent event) {
    Snacky.builder()
        .setView(findViewById(R.id.currencylist_coordinator))
        .setTextColor(getResources().getColor(R.color.snackTextColor))
        .error()
        .setText(com.neoscaler.cryptotrends.R.string.currencylist_remoteexception)
        .show();
    Timber.e(event.getThrowable(), event.getMessage());
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    int id = item.getItemId();

    Fragment fragment;
    Class fragmentClass = null;
    switch (id) {
      case R.id.nav_market:
        fragmentClass = CurrencyListFragment.class;
        mFirebaseAnalytics
            .setCurrentScreen(this, "CurrencyListFragment", null /* class override */);
        break;
      case R.id.nav_alerts:
        fragmentClass = AlertListFragment.class;
        mFirebaseAnalytics.setCurrentScreen(this, "AlertListFragment", null /* class override */);
        break;
      case R.id.nav_feedback:
        sendFeedbackMail();
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
      case R.id.nav_about:
        fragmentClass = AboutFragment.class;
        mFirebaseAnalytics.setCurrentScreen(this, "AboutFragment", null /* class override */);
        break;
      case R.id.nav_settings:
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }

    try {
      fragment = (Fragment) fragmentClass.newInstance();

      // Insert the fragment by replacing any existing fragment
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager
          .beginTransaction()
          .replace(R.id.fragmentContainer, fragment)
          .commit();

      // Title/Item.checked gets set in onResume in fragments
    } catch (Exception e) {
      Timber.e(e);
    }

    mDrawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void setNavigationMenuItemChecked(int itemId) {
    if (mNavigationView.getMenu().findItem(itemId) != null) {
      mNavigationView.getMenu().findItem(itemId).setChecked(true);
    }
  }

  @Override
  public void onBackPressed() {
    if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
      super.onBackPressed();
    } else {
      Toast.makeText(getBaseContext(), R.string.mainactivity_query_exit,
          Toast.LENGTH_SHORT).show();
    }
    back_pressed = System.currentTimeMillis();
  }

  private void sendFeedbackMail() {
    Intent send = new Intent(Intent.ACTION_SENDTO);
    String uriText = "mailto:" + Uri.encode("neoscale.software@gmail.com") +
        "?subject=" + Uri.encode("Crypto Trends - Feedback");
    send.setData(Uri.parse(uriText));
    startActivity(Intent.createChooser(send, "Send mail..."));
  }

}
