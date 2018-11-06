package com.ccclubs.zble.conn;

import android.bluetooth.BluetoothGattCallback;

import com.ccclubs.zble.exception.RxBleException;


/**
 * RxBleCallback
 *
 *@author LiuLiWei
 */
public abstract class RxBleCallback {

  private BluetoothGattCallback bluetoothGattCallback;

  public RxBleCallback() {}

  protected RxBleCallback setBluetoothGattCallback(BluetoothGattCallback bluetoothGattCallback) {
    this.bluetoothGattCallback = bluetoothGattCallback;
    return this;
  }

  protected BluetoothGattCallback getBluetoothGattCallback() {
    return this.bluetoothGattCallback;
  }

  public void onInitiatedSuccess() {
  }

  public abstract void onFailure(RxBleException var1);
}