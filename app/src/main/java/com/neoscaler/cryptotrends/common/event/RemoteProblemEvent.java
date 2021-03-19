package com.neoscaler.cryptotrends.common.event;

public class RemoteProblemEvent {

  String message;

  Throwable throwable;

  public RemoteProblemEvent(String message, Throwable throwable) {
    this.message = message;
    this.throwable = throwable;
  }

  public RemoteProblemEvent(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }
}
