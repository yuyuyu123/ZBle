package com.ccclubs.zble.scan;


import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;

/**
 *RxMacScanCallback
 *
 * @author LiuLiWei
 */
public abstract class RxMacScanCallback implements LeScanCallback {
  private String mac;

  protected RxMacScanCallback(String mac) {
    this.mac = mac;
    if(mac == null) {
      throw new IllegalArgumentException("start scan, mac can not be null!");
    }
  }

  public abstract void onMacScaned(BluetoothDevice var1, int var2, byte[] var3);

  public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
    if(this.mac.equalsIgnoreCase(device.getAddress())) {
      this.onMacScaned(device, rssi, scanRecord);
    }

  }
}
