package com.ccclubs.zble.scan;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.RequiresPermission;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RxNameScanCallback
 *
 * @author LiuLiWei
 */
public abstract class RxNameScanCallback extends RxPeriodScanCallback {
    private String name;
    private AtomicBoolean hasFound = new AtomicBoolean(false);

    public RxNameScanCallback(String name, long timeoutMillis) {
        super(timeoutMillis);
        this.name = name;
        if(name == null) {
            throw new IllegalArgumentException("start scan, device name can not be null!");
        }
    }

    @Override
    @RequiresPermission("android.permission.BLUETOOTH")
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if(!this.hasFound.get() && this.name.equals(device.getName())) {
            this.hasFound.set(true);
            this.rxBluetooth.stopScan(this);
            this.onDeviceFound(device, rssi, scanRecord);
        }
    }

    public abstract void onDeviceFound(BluetoothDevice var1, int var2, byte[] var3);
}
