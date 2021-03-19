package com.neoscaler.cryptotrends.application.ui.alert;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.CryptoTrendsApplication;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import com.neoscaler.cryptotrends.application.model.CustomAlert.SignalType;
import com.neoscaler.cryptotrends.application.model.projection.CustomAlertEntry;
import com.neoscaler.cryptotrends.application.network.jobs.CheckCustomAlertsManuallyJob;
import com.neoscaler.cryptotrends.application.ui.MainActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;

public class AlertListFragment extends Fragment {

  AlertListViewModel mAlertListViewModel;

  AlertListAdapter alertListAdapter;

  @BindView(R.id.alert_list)
  RecyclerView mRecyclerView;

  @BindView(R.id.btn_add_alert)
  FloatingActionButton fab;

  @BindView(R.id.alert_list_easyRefresh)
  SwipeRefreshLayout easyRefreshLayout;

  ModelMapper mModelMapper;

  private FirebaseAnalytics mFirebaseAnalytics;

  public AlertListFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    mModelMapper = new ModelMapper();
    getActivity().setTitle(getActivity().getResources().getString(R.string.menu_main_alerts));
    AlertListViewModel.Factory factory = new AlertListViewModel.Factory(
        ((CryptoTrendsApplication) getActivity().getApplication()).getDataRepository());
    mAlertListViewModel = ViewModelProviders.of(getActivity(), factory)
        .get(AlertListViewModel.class);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_alert_list, container, false);
    ButterKnife.bind(this, view);

    initFab();
    initView();
    initAdapter();
    initPullDownRefresh();

    return view;
  }

  private void initPullDownRefresh() {
    easyRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
      CheckCustomAlertsManuallyJob.startJob();
      easyRefreshLayout.setRefreshing(false);
    }, 1000));
  }

  private void initFab() {
    fab.setOnClickListener(v -> {
      Intent intent = new Intent(getActivity(), EditAddAlertActivity.class);
      startActivity(intent);
    });
  }

  private void initView() {
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
  }

  private void initAdapter() {
    alertListAdapter = new AlertListAdapter(R.layout.fragment_alert_list_line,
        R.layout.fragment_alert_list_subheader,
        null, getActivity());
    alertListAdapter.openLoadAnimation();
    alertListAdapter.bindToRecyclerView(mRecyclerView);
    alertListAdapter.setEmptyView(R.layout.fragment_alert_list_empty_view);
    mAlertListViewModel.getCustomAlerts().observe(this,
        newItems -> {
          List<CustomAlertEntry> entryList = new ArrayList<>();
          if (newItems.size() > 0) {
            for (CustomAlert alert : newItems) {
              entryList.add(mModelMapper.map(alert, CustomAlertEntry.class));
            }
            // TODO This can be implemented better :)
            HashMap<SignalType, Integer> headerList = new LinkedHashMap<>();
            for (int i = 0; i < entryList.size(); i++) {
              if (entryList.get(i).getSignalType() == SignalType.NONE) {
                headerList.put(entryList.get(i).getSignalType(), i);
                break;
              }
            }
            for (int i = 0; i < entryList.size(); i++) {
              if (entryList.get(i).getSignalType() == SignalType.BUY) {
                headerList.put(entryList.get(i).getSignalType(), i);
                break;
              }
            }
            for (int i = 0; i < entryList.size(); i++) {
              if (entryList.get(i).getSignalType() == SignalType.SELL) {
                headerList.put(entryList.get(i).getSignalType(), i);
                break;
              }
            }
            int addedSubheaders = 0;
            for (Map.Entry<SignalType, Integer> entry : headerList.entrySet()) {
              entryList.add(entry.getValue() + addedSubheaders++,
                  new CustomAlertEntry(true, "", entry.getKey()));
            }
          }
          alertListAdapter.replaceData(entryList);
        }
    );
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle(getActivity().getResources().getString(R.string.menu_main_alerts));
    View toolbarMarketCapLayout = getActivity().findViewById(R.id.toolbarMarketCapLayout);
    toolbarMarketCapLayout.setVisibility(View.GONE);

    ((MainActivity) getActivity()).setNavigationMenuItemChecked(R.id.nav_alerts);
    mFirebaseAnalytics
        .setCurrentScreen(getActivity(), getClass().getSimpleName(), getClass().getSimpleName());
  }
}
