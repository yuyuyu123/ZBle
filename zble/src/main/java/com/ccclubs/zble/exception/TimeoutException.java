package com.ccclubs.zble.exception;

/**
 *TimeoutException
 *
 * @author LiuLiWei
 */
public class TimeoutException extends RxBleException {
  public TimeoutException() {
    super(1, "Timeout Exception Occurred! ");
  }
}