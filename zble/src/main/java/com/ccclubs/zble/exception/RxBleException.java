package com.ccclubs.zble.exception;

import java.io.Serializable;

/**
 *RxBleException
 *
 * @author LiuLiWei
 */
public abstract class RxBleException implements Serializable {
  private static final long serialVersionUID = 8004414918500865564L;
  public static final int ERROR_CODE_TIMEOUT = 1;
  public static final int ERROR_CODE_INITIAL = 101;
  public static final int ERROR_CODE_GATT = 201;
  public static final int GATT_CODE_OTHER = 301;
  public static final TimeoutException TIMEOUT_EXCEPTION = new TimeoutException();
  private int code;
  private String description;

  public RxBleException(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return this.code;
  }

  public RxBleException setCode(int code) {
    this.code = code;
    return this;
  }

  public String getDescription() {
    return this.description;
  }

  public RxBleException setDescription(String description) {
    this.description = description;
    return this;
  }

  public String toString() {
    return "RxBleException{code=" + this.code + ", description=\'" + this.description + '\'' + '}';
  }
}

