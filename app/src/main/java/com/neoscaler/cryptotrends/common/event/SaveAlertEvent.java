package com.neoscaler.cryptotrends.common.event;

import com.neoscaler.cryptotrends.application.model.CustomAlert;

public class SaveAlertEvent {

  private CustomAlert newCustomAlert;

  public SaveAlertEvent(CustomAlert newCustomAlert) {
    this.newCustomAlert = newCustomAlert;
  }

  public CustomAlert getNewCustomAlert() {
    return newCustomAlert;
  }

  public void setNewCustomAlert(CustomAlert newCustomAlert) {
    this.newCustomAlert = newCustomAlert;
  }
}
