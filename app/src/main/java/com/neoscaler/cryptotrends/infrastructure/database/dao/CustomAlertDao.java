package com.neoscaler.cryptotrends.infrastructure.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.neoscaler.cryptotrends.application.model.CustomAlert;
import java.util.List;

@Dao
public interface CustomAlertDao {

  @Insert
  void insert(CustomAlert customAlert);

  @Update
  void update(CustomAlert customAlert);

  @Delete
  void deleteAlert(CustomAlert customAlert);

  @Query("SELECT * FROM CustomAlert ORDER BY signalType, currencyId")
  LiveData<List<CustomAlert>> getAlertsLiveData();

  @Query("SELECT * FROM CustomAlert WHERE active = 1 ORDER BY currencyId")
  List<CustomAlert> getActiveAlerts();

  @Query("SELECT * FROM CustomAlert WHERE id = :id")
  CustomAlert getAlert(long id);

  @Query("SELECT * FROM CustomAlert WHERE id = :id")
  LiveData<CustomAlert> fetchAsyncAlert(long id);

}
