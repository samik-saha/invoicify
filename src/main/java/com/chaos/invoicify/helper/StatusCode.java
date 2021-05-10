package com.chaos.invoicify.helper;

public enum StatusCode {
  SUCCESS(1),
  DUPLICATE(2),
  NONAME(3),
  OTHER(4);

  private int status;

  StatusCode(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
