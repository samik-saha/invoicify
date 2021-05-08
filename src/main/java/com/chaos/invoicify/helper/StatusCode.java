package com.chaos.invoicify.helper;

public enum StatusCode {
  SUCCESS(1),
  DUPLICATE(2),
  OTHER(3);

  private int status;

  StatusCode(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
