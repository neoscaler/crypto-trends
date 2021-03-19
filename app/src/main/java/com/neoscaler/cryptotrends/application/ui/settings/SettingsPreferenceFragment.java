package com.neoscaler.cryptotrends.application.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.neoscaler.cryptotrends.R;
import com.takisoft.preferencex.PreferenceFragmentCompat;


public class SettingsPreferenceFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.pref_general, rootKey);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    return super.onCreateView(inflater, container, savedInstanceState);
  }

}
