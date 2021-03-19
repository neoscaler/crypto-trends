package com.neoscaler.cryptotrends.application.ui.currencylist;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.CryptoTrendsApplication;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.network.jobs.FetchTickerResultsJob;
import com.neoscaler.cryptotrends.application.network.jobs.GlobalMarketDataJob;
import com.neoscaler.cryptotrends.application.ui.MainActivity;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.neoscaler.cryptotrends.common.event.CurrencyListUpdatedEvent;
import com.pixplicity.easyprefs.library.Prefs;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CurrencyListFragment extends Fragment {

  private CurrencyListViewModel mCurrencyListViewModel;

  private CurrencyListAdapter mCurrencyListAdapter;

  @BindView(R.id.currency_list)
  RecyclerView mRecyclerView;

  @BindView(R.id.currency_list_refreshlayout)
  SwipeRefreshLayout mSwipeRefreshLayout;

  @BindView(R.id.currencylist_bottom_navigation)
  BottomNavigationView bottomNavigationView;

  private View emptyViewAll;
  private View emptyViewFavorites;
  private View emptyViewWatchlist;

  private FirebaseAnalytics mFirebaseAnalytics;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    EventBus.getDefault().register(this);
    setHasOptionsMenu(true);
    initViewModel();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_currency_list, container, false);
    ButterKnife.bind(this, view);

    initView();
    initAdapter();
    initPullDownRefresh();
    initEmptyViews();

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initBottomNavigation();
    startInitialPriceUpdateIfNecessary();
  }

  private void initBottomNavigation() {
    bottomNavigationView.setOnNavigationItemSelectedListener(
        item -> {
          // TODO Switch EmptyView for Favs+Watchlist
          switch (item.getItemId()) {
            case R.id.action_bnav_all:
              mCurrencyListViewModel.setActiveTab(CurrencyListViewModel.BottomTabView.ALL);
              mCurrencyListAdapter.setEmptyView(emptyViewAll);
              Prefs.putString(SharedPrefsKeys.CURRLIST_BOTTOMNAV_ACTIVE, "ALL");
              break;
            case R.id.action_bnav_favorites:
              mCurrencyListViewModel.setActiveTab(CurrencyListViewModel.BottomTabView.FAVORITE);
              mCurrencyListAdapter.setEmptyView(emptyViewFavorites);
              Prefs.putString(SharedPrefsKeys.CURRLIST_BOTTOMNAV_ACTIVE, "FAVS");
              break;
            case R.id.action_bnav_watchlist:
              mCurrencyListViewModel.setActiveTab(CurrencyListViewModel.BottomTabView.WATCHLIST);
              mCurrencyListAdapter.setEmptyView(emptyViewWatchlist);
              Prefs.putString(SharedPrefsKeys.CURRLIST_BOTTOMNAV_ACTIVE, "WATCHED");
              break;
            default:
              throw new UnsupportedOperationException("Unsupported bottom navigation item");
          }
          mCurrencyListAdapter.rebuildList();
          return true;
        });
    //Preselect bottom tab
    String activeTab = Prefs.getString(SharedPrefsKeys.CURRLIST_BOTTOMNAV_ACTIVE, "ALL");
    switch (activeTab) {
      case "FAVS":
        bottomNavigationView.setSelectedItemId(R.id.action_bnav_favorites);
        break;
      case "WATCHED":
        bottomNavigationView.setSelectedItemId(R.id.action_bnav_watchlist);
        break;
      default:
        bottomNavigationView.setSelectedItemId(R.id.action_bnav_all);
    }
  }

  private void initViewModel() {
    CurrencyListViewModel.Factory factory = new CurrencyListViewModel.Factory(
        ((CryptoTrendsApplication) getActivity().getApplication()).getDataRepository());
    mCurrencyListViewModel = ViewModelProviders.of(getActivity(), factory)
        .get(CurrencyListViewModel.class);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_currency_list, menu);

    // Add search
    MenuItem search = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
    search(searchView);
  }

  private void search(SearchView searchView) {
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        mCurrencyListAdapter.getFilter().filter(newText);
        return true;
      }
    });
  }

  private void initEmptyViews() {
    emptyViewAll = getLayoutInflater().inflate(R.layout.fragment_currency_list_empty_view, null);
    emptyViewFavorites = getLayoutInflater()
        .inflate(R.layout.fragment_currency_list_empty_view_favorites, null);
    emptyViewWatchlist = getLayoutInflater()
        .inflate(R.layout.fragment_currency_list_empty_view_watchlist, null);
  }

  private void startInitialPriceUpdateIfNecessary() {
    if (!mCurrencyListViewModel.isInitialPriceUpdateDone()) {
      GlobalMarketDataJob.startJob();
      FetchTickerResultsJob.startJob();
      mCurrencyListViewModel.setInitialPriceUpdateDone(true);
    }
  }

  private void initAdapter() {
    mCurrencyListAdapter = new CurrencyListAdapter(R.layout.fragment_currency_list_line,
        mCurrencyListViewModel.getPersistedCurrencies().getValue(), getActivity(),
        mCurrencyListViewModel);
    mCurrencyListAdapter.openLoadAnimation();
    mCurrencyListAdapter
        .setHeaderView(getLayoutInflater().inflate(R.layout.fragment_currency_list_header, null));
    mCurrencyListAdapter.bindToRecyclerView(mRecyclerView);
    mCurrencyListViewModel.getPersistedCurrencies().observe(this,
        newItems -> mCurrencyListAdapter.setNewData(newItems));
    mCurrencyListViewModel.getPersistedCurrenciesFiltered().observe(this,
        newItems -> mCurrencyListAdapter.setNewData(newItems));
  }

  private void initPullDownRefresh() {
    mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().post(() -> {
      GlobalMarketDataJob.startJob();
      FetchTickerResultsJob.startJob();
      mFirebaseAnalytics.logEvent("update_currencylist_manual", null);
    }));
  }

  private void initView() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle(null);
    ((MainActivity) getActivity()).setNavigationMenuItemChecked(R.id.nav_market);
    mFirebaseAnalytics
        .setCurrentScreen(getActivity(), getClass().getSimpleName(), getClass().getSimpleName());

    View toolbarMarketCapLayout = getActivity().findViewById(R.id.toolbarMarketCapLayout);
    if (toolbarMarketCapLayout != null) {
      toolbarMarketCapLayout.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    View toolbarMarketCapLayout = getActivity().findViewById(R.id.toolbarMarketCapLayout);
    if (toolbarMarketCapLayout != null) {
      toolbarMarketCapLayout.setVisibility(View.GONE);
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onMessageEventUpdated(CurrencyListUpdatedEvent event) {
    mSwipeRefreshLayout.setRefreshing(false);
  }
}