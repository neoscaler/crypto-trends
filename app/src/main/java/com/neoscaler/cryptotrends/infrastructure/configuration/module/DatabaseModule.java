package com.neoscaler.cryptotrends.infrastructure.configuration.module;

import android.app.Application;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.neoscaler.cryptotrends.application.repository.DataRepository;
import com.neoscaler.cryptotrends.common.SharedPrefsKeys;
import com.neoscaler.cryptotrends.database.AppDatabase;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CryptoCurrencyDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CurrencyUserDataDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CustomAlertDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.GlobalMarketDataDao;
import com.pixplicity.easyprefs.library.Prefs;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.modelmapper.ModelMapper;

@Module
public class DatabaseModule {

  public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL(
          "ALTER TABLE PersistedCurrency ADD COLUMN baseCurrency TEXT NOT NULL DEFAULT \"\"");
      database.execSQL(
          "ALTER TABLE PersistedCurrency ADD COLUMN priceBaseCurrency REAL NOT NULL DEFAULT 0");
    }
  };
  public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL(
          "ALTER TABLE PersistedCurrency ADD COLUMN _24hVolumeBaseCurrency REAL NOT NULL DEFAULT \"\"");
      database.execSQL(
          "ALTER TABLE PersistedCurrency ADD COLUMN marketCapBaseCurrency REAL NOT NULL DEFAULT 0");
    }
  };
  public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("ALTER TABLE PersistedCurrency ADD COLUMN high24Hour REAL");
      database.execSQL("ALTER TABLE PersistedCurrency ADD COLUMN low24Hour REAL");
      database.execSQL("ALTER TABLE PersistedCurrency ADD COLUMN lastUpdatedCryptoCompare INTEGER");
    }
  };
  public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      database.execSQL("CREATE TABLE IF NOT EXISTS CustomAlert " +
          "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `currencyId` TEXT NOT NULL, " +
          "`currencyName` TEXT NOT NULL, `currencySymbol` TEXT NOT NULL, `alertType` INTEGER NOT NULL, "
          +
          "`priceBase` REAL NOT NULL, `baseCurrency` TEXT NOT NULL, `priceThresholdBaseCurrency` REAL NOT NULL, "
          +
          "`priceLastChecked` REAL NOT NULL, `priceThresholdSatoshi` REAL NOT NULL, `active` INTEGER NOT NULL, "
          +
          "`signalType` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `firedAt` INTEGER, `lastChecked` INTEGER, "
          +
          "`notes` TEXT)");
    }
  };
  public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
    @Override
    public void migrate(SupportSQLiteDatabase database) {
      // Remove old global market globalDataResultContent
      Prefs.remove(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP_LASTUPDATED);
      Prefs.remove(SharedPrefsKeys.SMARTALARMS_GLOBALMARKETCAP);
      database.execSQL("DELETE FROM GlobalMarketData");

      // Update table
      database
          .execSQL(
              "ALTER TABLE GlobalMarketData ADD COLUMN totalMarketCapBaseCurrency INTEGER NOT NULL DEFAULT 0");
      database
          .execSQL(
              "ALTER TABLE GlobalMarketData ADD COLUMN total24hVolumeBaseCurrency INTEGER NOT NULL DEFAULT 0");
      database.execSQL(
          "ALTER TABLE GlobalMarketData ADD COLUMN baseCurrency TEXT NOT NULL DEFAULT \"\"");
    }
  };

  @Singleton
  @Provides
  AppDatabase provideDb(Application app) {
    return Room.databaseBuilder(app, AppDatabase.class, "cryptotrends_db")
        // Destructive, bc. 7_8 is too big to migrate!
        .fallbackToDestructiveMigration()
        .addMigrations(MIGRATION_2_3)
        .addMigrations(MIGRATION_3_4)
        .addMigrations(MIGRATION_4_5)
        .addMigrations(MIGRATION_5_6)
        .addMigrations(MIGRATION_6_7)
        // 7_8 is destructive!
        .build();
  }

  @Singleton
  @Provides
  GlobalMarketDataDao provideGlobalMarketDataDao(AppDatabase db) {
    return db.globalMarketDataDaoCurrencyDao();
  }

  @Singleton
  @Provides
  CryptoCurrencyDao providePersistedCurrencyDao(AppDatabase db) {
    return db.persistedCurrencyDao();
  }

  @Singleton
  @Provides
  CurrencyUserDataDao provideCurrencyUserDataDao(AppDatabase db) {
    return db.currencyUserMarkerDao();
  }

  @Singleton
  @Provides
  CustomAlertDao provideCustomAlertDao(AppDatabase db) {
    return db.customAlertDao();
  }

  @Singleton
  @Provides
  DataRepository provideDataRepository(CryptoCurrencyDao cryptoCurrencyDao,
      GlobalMarketDataDao globalMarketDataDao,
      CurrencyUserDataDao currencyUserDataDao,
      CustomAlertDao customAlertDao,
      ModelMapper modelMapper) {
    return new DataRepository(cryptoCurrencyDao, globalMarketDataDao, currencyUserDataDao,
        customAlertDao, modelMapper);
  }

}
