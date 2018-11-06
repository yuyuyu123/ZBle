package com.ccclubs.zble.conn;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 *RxBleCharaCallback
 *
 *@author LiuLiWei
 */
public abstract class RxBleCharaCallback extends RxBleCallback {

  public RxBleCharaCallback() {}

  public abstract void onSuccess(BluetoothGattCharacteristic var1);
}
