package com.neoscaler.cryptotrends.infrastructure.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.neoscaler.cryptotrends.application.model.GlobalMarketData;

@Dao
public interface GlobalMarketDataDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(GlobalMarketData dataEntry);

  @Query("SELECT * FROM GlobalMarketData ORDER BY lastUpdated DESC LIMIT 1")
  LiveData<GlobalMarketData> loadMostCurrent();

}
