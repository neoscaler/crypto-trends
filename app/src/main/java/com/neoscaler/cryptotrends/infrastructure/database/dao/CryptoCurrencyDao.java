package com.neoscaler.cryptotrends.infrastructure.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;
import com.neoscaler.cryptotrends.application.model.CryptoCurrency;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyBasic;
import com.neoscaler.cryptotrends.application.model.projection.CurrencyListEntry;
import io.reactivex.Single;
import java.util.List;

@Dao
public interface CryptoCurrencyDao {

  @Query("DELETE FROM CryptoCurrency")
  void deleteAllCurrencies();

  // TODO: Replace with real insertUpdate?
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insertCurrencies(List<CryptoCurrency> currencies);

  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query(
      "SELECT CryptoCurrency.*, favorite, watched " +
          "FROM CryptoCurrency LEFT JOIN CurrencyUserData " +
          "ON CryptoCurrency.id = CurrencyUserData.id " +
          "ORDER BY marketCapRank ASC")
  LiveData<List<CurrencyListEntry>> loadAllCurrencies();

  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query("SELECT CryptoCurrency.*, favorite, watched " +
      "FROM CryptoCurrency LEFT JOIN CurrencyUserData " +
      "ON CryptoCurrency.id = CurrencyUserData.id " +
      "WHERE CryptoCurrency.id = :id")
  LiveData<CurrencyListEntry> fetchCurrency(String id);

  @Update
  void updateCurrency(CryptoCurrency cryptoCurrency);

  @Query("SELECT * FROM CryptoCurrency WHERE id = :id")
  CryptoCurrency loadFullCurrency(String id);

  @Query("SELECT * FROM CryptoCurrency WHERE id = :id")
  Single<CryptoCurrency> loadFullCurrencyAsync(String id);

  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query("SELECT id, name, symbol, marketCapRank FROM CryptoCurrency")
  LiveData<List<CurrencyBasic>> getAvailableCurrencies();
}
