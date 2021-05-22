package com.chaos.invoicify.helper;

public enum StatusCode {
  SUCCESS(1),
  DUPLICATE(2),
  NONAME(3),
  OTHER(4),
  NOTFOUND(5),
  FOUND(6);

  private int status;

  StatusCode(int status) {
    this.status = status;
  }

}
