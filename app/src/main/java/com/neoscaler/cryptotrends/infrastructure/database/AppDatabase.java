package com.neoscaler.cryptotrends.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.neoscaler.cryptotrends.application.model.CryptoCurrency;
import com.neoscaler.cryptotrends.application.model.CurrencyUserData;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import com.neoscaler.cryptotrends.application.model.GlobalMarketData;
import com.neoscaler.cryptotrends.database.converter.AlarmTypeConverter;
import com.neoscaler.cryptotrends.database.converter.DateTimeTypeConverter;
import com.neoscaler.cryptotrends.database.converter.SignalTypeConverter;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CryptoCurrencyDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CurrencyUserDataDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.CustomAlertDao;
import com.neoscaler.cryptotrends.infrastructure.database.dao.GlobalMarketDataDao;

@Database(entities = {
    CryptoCurrency.class,
    GlobalMarketData.class,
    CurrencyUserData.class,
    CustomAlert.class},
    version = 8)
@TypeConverters({DateTimeTypeConverter.class, AlarmTypeConverter.class, SignalTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {

  public abstract CryptoCurrencyDao persistedCurrencyDao();

  public abstract GlobalMarketDataDao globalMarketDataDaoCurrencyDao();

  public abstract CurrencyUserDataDao currencyUserMarkerDao();

  public abstract CustomAlertDao customAlertDao();

}
