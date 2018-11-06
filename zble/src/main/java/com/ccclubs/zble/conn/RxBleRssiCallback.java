package com.ccclubs.zble.conn;

/**
 *RxBleRssiCallback
 *
 *@author LiuLiWei
 */
public abstract class RxBleRssiCallback extends RxBleCallback {
  public RxBleRssiCallback() {
  }

  public abstract void onSuccess(int var1);
}
