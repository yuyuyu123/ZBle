package com.ccclubs.zble.exception;
/**
 *InitiatedException
 *
 * @author LiuLiWei
 */
public class InitiatedException extends RxBleException {
  public InitiatedException() {
    super(101, "Initiated Exception Occurred! ");
  }
}