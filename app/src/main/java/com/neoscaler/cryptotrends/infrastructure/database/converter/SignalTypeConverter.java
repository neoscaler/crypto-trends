package com.neoscaler.cryptotrends.database.converter;

import androidx.room.TypeConverter;
import com.neoscaler.cryptotrends.application.model.CustomAlert.SignalType;

public class SignalTypeConverter {

  @TypeConverter
  public static SignalType toSignalType(int code) {
    if (code == SignalType.NONE.getCode()) {
      return SignalType.NONE;
    } else if (code == SignalType.BUY.getCode()) {
      return SignalType.BUY;
    } else if (code == SignalType.SELL.getCode()) {
      return SignalType.SELL;
    }
    return SignalType.NONE;
  }

  @TypeConverter
  public static int toInt(SignalType object) {
    return object == null ? 0 : object.getCode();
  }

}
