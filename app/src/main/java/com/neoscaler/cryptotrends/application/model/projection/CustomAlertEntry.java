package com.neoscaler.cryptotrends.application.model.projection;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.neoscaler.cryptotrends.application.model.CustomAlert.AlertType;
import com.neoscaler.cryptotrends.application.model.CustomAlert.SignalType;
import org.joda.time.DateTime;

// TODO Lombokify
public class CustomAlertEntry extends SectionEntity<CustomAlertEntry> {

  private long id;

  private String currencyId;

  private String currencyName;

  private String currencySymbol;

  private AlertType alertType;

  private double priceBase;

  private String baseCurrency;

  private double priceThresholdBaseCurrency;

  private double priceLastChecked;

  private double priceThresholdSatoshi;

  private boolean active = true;

  private SignalType signalType;

  private DateTime createdAt;

  private DateTime firedAt;

  private DateTime lastChecked;

  private String notes;

  public CustomAlertEntry(boolean isHeader, String header, SignalType signalType) {
    super(isHeader, header);
    this.signalType = signalType;
  }

  public CustomAlertEntry(CustomAlertEntry customAlert) {
    super(customAlert);
  }

  public CustomAlertEntry() {
    super(null);
  }

  public double getPriceLastChecked() {
    return priceLastChecked;
  }

  public void setPriceLastChecked(double priceLastChecked) {
    this.priceLastChecked = priceLastChecked;
  }

  public String getCurrencyName() {
    return currencyName;
  }

  public void setCurrencyName(String currencyName) {
    this.currencyName = currencyName;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public DateTime getLastChecked() {
    return lastChecked;
  }

  public void setLastChecked(DateTime lastChecked) {
    this.lastChecked = lastChecked;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(String currencyId) {
    this.currencyId = currencyId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public AlertType getAlertType() {
    return alertType;
  }

  public void setAlertType(AlertType alertType) {
    this.alertType = alertType;
  }

  public double getPriceBase() {
    return priceBase;
  }

  public void setPriceBase(double priceBase) {
    this.priceBase = priceBase;
  }

  public String getBaseCurrency() {
    return baseCurrency;
  }

  public void setBaseCurrency(String baseCurrency) {
    this.baseCurrency = baseCurrency;
  }

  public double getPriceThresholdBaseCurrency() {
    return priceThresholdBaseCurrency;
  }

  public void setPriceThresholdBaseCurrency(double priceThresholdBaseCurrency) {
    this.priceThresholdBaseCurrency = priceThresholdBaseCurrency;
  }

  public double getPriceThresholdSatoshi() {
    return priceThresholdSatoshi;
  }

  public void setPriceThresholdSatoshi(double priceThresholdSatoshi) {
    this.priceThresholdSatoshi = priceThresholdSatoshi;
  }

  public SignalType getSignalType() {
    return signalType;
  }

  public void setSignalType(SignalType signalType) {
    this.signalType = signalType;
  }

  public DateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(DateTime createdAt) {
    this.createdAt = createdAt;
  }

  public DateTime getFiredAt() {
    return firedAt;
  }

  public void setFiredAt(DateTime firedAt) {
    this.firedAt = firedAt;
  }

}
