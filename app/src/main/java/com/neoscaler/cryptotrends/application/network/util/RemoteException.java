package com.neoscaler.cryptotrends.application.network.util;

import retrofit2.Response;

public class RemoteException extends Exception {

  private final Response response;

  public RemoteException(Response response) {
    this.response = response;
  }

  public Response getResponse() {
    return response;
  }
}
