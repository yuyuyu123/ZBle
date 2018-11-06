package com.ccclubs.zble.exception;


import android.bluetooth.BluetoothGatt;

/**
 *ConnectException
 *
 * @author LiuLiWei
 */
public class ConnectException extends RxBleException {
  private BluetoothGatt bluetoothGatt;
  private int gattStatus;

  public ConnectException(BluetoothGatt bluetoothGatt, int gattStatus) {
    super(201, "Gatt Exception Occurred! ");
    this.bluetoothGatt = bluetoothGatt;
    this.gattStatus = gattStatus;
  }

  public int getGattStatus() {
    return this.gattStatus;
  }

  public ConnectException setGattStatus(int gattStatus) {
    this.gattStatus = gattStatus;
    return this;
  }

  public BluetoothGatt getBluetoothGatt() {
    return this.bluetoothGatt;
  }

  public ConnectException setBluetoothGatt(BluetoothGatt bluetoothGatt) {
    this.bluetoothGatt = bluetoothGatt;
    return this;
  }

  public String toString() {
    return "ConnectException{gattStatus=" + this.gattStatus + ", bluetoothGatt=" + this.bluetoothGatt + "} " + super.toString();
  }
}
