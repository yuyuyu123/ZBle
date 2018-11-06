package com.ccclubs.zble.scan;


import android.bluetooth.BluetoothDevice;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *RxPeriodMacScanCallback
 *
 * @author LiuLiWei
 */
public abstract class RxPeriodMacScanCallback extends RxPeriodScanCallback {
  private String mac;
  private AtomicBoolean hasFound = new AtomicBoolean(false);

  public RxPeriodMacScanCallback(String mac, long timeoutMillis) {
    super(timeoutMillis);
    this.mac = mac;
    if(mac == null) {
      throw new IllegalArgumentException("start scan, mac can not be null!");
    }
  }

  public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    if(!this.hasFound.get() && this.mac.equalsIgnoreCase(device.getAddress())) {
      this.hasFound.set(true);
      this.rxBluetooth.stopScan(this);
      this.onDeviceFound(device, rssi, scanRecord);
    }

  }

  public abstract void onDeviceFound(BluetoothDevice var1, int var2, byte[] var3);
}
