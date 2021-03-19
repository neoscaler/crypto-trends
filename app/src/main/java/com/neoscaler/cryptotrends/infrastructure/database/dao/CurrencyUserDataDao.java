package com.neoscaler.cryptotrends.infrastructure.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.neoscaler.cryptotrends.application.model.CurrencyUserData;

@Dao
public interface CurrencyUserDataDao {

  @Insert
  void insertNewCurrencyUserData(CurrencyUserData currencyUserData);

  @Update
  void updateCurrencyUserData(CurrencyUserData currencyUserData);

  @Query("SELECT * FROM CurrencyUserData WHERE id = :id")
  CurrencyUserData getCurrencyUserData(String id);

}
