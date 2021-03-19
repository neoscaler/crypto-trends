package com.neoscaler.cryptotrends.infrastructure.configuration;

import android.app.Application;
import com.neoscaler.cryptotrends.CryptoTrendsApplication;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import com.neoscaler.cryptotrends.infrastructure.configuration.module.AppModule;
import com.neoscaler.cryptotrends.infrastructure.configuration.module.DatabaseModule;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import javax.inject.Singleton;

@Singleton
@Component(modules = {
    AndroidSupportInjectionModule.class,
    DatabaseModule.class,
    AppModule.class})
public interface AppComponent extends AndroidInjector<CryptoTrendsApplication> {

  DataRepository getDataRepository();

  // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this).build().inject(this);
  // never having to instantiate any modules or say which module we are passing the application to.
  // Application will just be provided into our app graph now.
  @Component.Builder
  interface Builder {

    @BindsInstance
    AppComponent.Builder application(Application application);

    AppComponent build();
  }
}
