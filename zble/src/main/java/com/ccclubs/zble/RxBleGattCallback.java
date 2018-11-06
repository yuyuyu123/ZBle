package com.ccclubs.zble;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Build;

import com.ccclubs.zble.exception.RxBleException;

/**
 *RxBleGattCallback
 *
 * @author LiuLiWei
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class RxBleGattCallback extends BluetoothGattCallback {

  public RxBleGattCallback() {}

  public abstract void onConnectSuccess(BluetoothGatt gatt, int status);

  public abstract void onServicesDiscovered(BluetoothGatt gatt, int status);

  public abstract void onConnectFailure(RxBleException exception);
}
