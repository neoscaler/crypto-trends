package com.neoscaler.cryptotrends.database.converter;

import androidx.room.TypeConverter;
import com.neoscaler.cryptotrends.application.model.CustomAlert.AlertType;

public class AlarmTypeConverter {

  @TypeConverter
  public static AlertType toAlarmType(int code) {
    if (code == AlertType.ONE_TIME.getCode()) {
      return AlertType.ONE_TIME;
    } else if (code == AlertType.REPEATING.getCode()) {
      return AlertType.REPEATING;
    }
    return AlertType.ONE_TIME;
  }

  @TypeConverter
  public static int toInt(AlertType object) {
    return object == null ? -1 : object.getCode();
  }

}
