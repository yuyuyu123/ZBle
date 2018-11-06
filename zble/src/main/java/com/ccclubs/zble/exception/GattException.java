package com.ccclubs.zble.exception;

/**
 *GattException
 *
 * @author LiuLiWei
 */
public class GattException extends RxBleException {
  private int gattStatus;

  public GattException(int gattStatus) {
    super(201, "Gatt Exception Occurred! ");
    this.gattStatus = gattStatus;
  }

  public int getGattStatus() {
    return this.gattStatus;
  }

  public GattException setGattStatus(int gattStatus) {
    this.gattStatus = gattStatus;
    return this;
  }

  public String toString() {
    return "GattException{gattStatus=" + this.gattStatus + "} " + super.toString();
  }
}
