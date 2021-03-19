package com.neoscaler.cryptotrends.infrastructure.configuration.module;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.neoscaler.cryptotrends.application.ui.currencylist.CurrencyListViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

  @Binds
  @IntoMap
  @ViewModelKey(CurrencyListViewModel.class)
  abstract ViewModel bindSavedCurrencyViewModel(CurrencyListViewModel userViewModel);

  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(CurrencyListViewModel.Factory factory);
}
