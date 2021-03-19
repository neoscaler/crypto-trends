package com.neoscaler.cryptotrends.infrastructure.configuration.module;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.joda.time.DateTime;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

/**
 * This is where you will inject application-wide dependencies.
 */
@Module(includes = {ViewModelModule.class, DatabaseModule.class})
public class AppModule {

/*    @Binds
    abstract Context bindContext(Application application);*/

  @Provides
  Context provideContext(Application application) {
    return application.getApplicationContext();
  }

  @Singleton
  @Provides
  ModelMapper provideModelmapper() {
    Converter<Integer, DateTime> timestampToDateTime = new AbstractConverter<Integer, DateTime>() {
      protected DateTime convert(Integer source) {
        return source == null ? null : new DateTime(source.intValue());
      }
    };
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.addConverter(timestampToDateTime);
    return modelMapper;
  }

}
