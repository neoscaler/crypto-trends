package com.neoscaler.cryptotrends.common.event;

import com.neoscaler.cryptotrends.application.model.CryptoCurrency;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CurrencyListUpdatedEvent {

  private List<CryptoCurrency> tickerResultList = new ArrayList<>();

}
