package com.neoscaler.cryptotrends.application.ui.about;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.neoscaler.cryptotrends.R;
import com.neoscaler.cryptotrends.application.ui.MainActivity;
import timber.log.Timber;

public class AboutFragment extends Fragment {

  @BindView(R.id.textViewWebsiteInfo)
  TextView textViewWebsiteInfo;

  @BindView(R.id.textViewUsedThirdParty)
  TextView textView3rdParty;

  @BindView(R.id.textViewVersion)
  TextView textViewVersion;

  private FirebaseAnalytics mFirebaseAnalytics;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_about, container, false);
    ButterKnife.bind(this, view);
    return view;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    try {
      PackageInfo pinfo = getActivity().getPackageManager()
          .getPackageInfo(getActivity().getPackageName(), 0);
      String versionNumber = pinfo.versionName;
      int versionCode = pinfo.versionCode;
      textViewVersion.setText("v" + versionNumber + " (" + versionCode + ")");
    } catch (PackageManager.NameNotFoundException e) {
      Timber.e(e);
    }

    textViewWebsiteInfo.setText(Html.fromHtml(getString(R.string.about_websiteinfo)));
    textViewWebsiteInfo.setMovementMethod(LinkMovementMethod.getInstance());
    textView3rdParty.setText(Html.fromHtml(getString(R.string.about_thirdparty)));
    textView3rdParty.setMovementMethod(LinkMovementMethod.getInstance());
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().setTitle(getActivity().getResources().getString(R.string.menu_main_about));
    ((MainActivity) getActivity()).setNavigationMenuItemChecked(R.id.nav_about);
    mFirebaseAnalytics
        .setCurrentScreen(getActivity(), getClass().getSimpleName(), getClass().getSimpleName());
  }
}
