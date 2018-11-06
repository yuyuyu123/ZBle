package com.ccclubs.zble.conn;

import android.bluetooth.BluetoothGattDescriptor;

/**
 *RxBleDescriptorCallback
 *
 *@author LiuLiWei
 */
public abstract class RxBleDescriptorCallback extends RxBleCallback {
  public RxBleDescriptorCallback() {
  }

  public abstract void onSuccess(BluetoothGattDescriptor var1);
}