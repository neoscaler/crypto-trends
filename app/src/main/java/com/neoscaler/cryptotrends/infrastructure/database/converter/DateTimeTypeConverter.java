package com.neoscaler.cryptotrends.database.converter;

import androidx.room.TypeConverter;
import org.joda.time.DateTime;

public class DateTimeTypeConverter {

  @TypeConverter
  // * 1000L because Unix time is seconds, Java time ms
  public static DateTime toDateTime(long value) {
    return value != -1 ? new DateTime(value) : null;
  }

  @TypeConverter
  public static long toTimestamp(DateTime value) {
    // TODO FIXME: This should be also seconds in the database?
    return value == null ? -1 : value.getMillis();
  }

}
