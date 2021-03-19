package com.neoscaler.cryptotrends.common.event;

public class RemoteProblemDetailEvent {

  String message;

  Throwable throwable;

  public RemoteProblemDetailEvent(String message, Throwable throwable) {
    this.message = message;
    this.throwable = throwable;
  }

  public RemoteProblemDetailEvent(String message) {
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
