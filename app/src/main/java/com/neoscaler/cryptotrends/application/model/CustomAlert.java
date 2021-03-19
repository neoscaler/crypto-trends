package com.neoscaler.cryptotrends.application.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.neoscaler.cryptotrends.database.converter.AlarmTypeConverter;
import com.neoscaler.cryptotrends.database.converter.DateTimeTypeConverter;
import com.neoscaler.cryptotrends.database.converter.SignalTypeConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.joda.time.DateTime;

@Entity
@Data
public class CustomAlert {

  @PrimaryKey(autoGenerate = true)
  private long id;

  private String currencyId;

  private String currencyName;

  private String currencySymbol;

  @TypeConverters({AlarmTypeConverter.class})
  private AlertType alertType;

  private double priceBase;

  private String baseCurrency;

  private double priceThresholdBaseCurrency;

  private double priceLastChecked;

  private boolean active = true;

  @TypeConverters({SignalTypeConverter.class})
  private SignalType signalType;

  @TypeConverters({DateTimeTypeConverter.class})
  private DateTime createdAt;

  @TypeConverters({DateTimeTypeConverter.class})
  private DateTime firedAt;

  @TypeConverters({DateTimeTypeConverter.class})
  private DateTime lastChecked;

  private String notes;

  @AllArgsConstructor
  public enum SignalType {
    NONE(0), BUY(1), SELL(2);

    @Getter
    private int code;

  }

  @AllArgsConstructor
  public enum AlertType {
    ONE_TIME(0), REPEATING(1);

    @Getter
    private int code;

  }
}
