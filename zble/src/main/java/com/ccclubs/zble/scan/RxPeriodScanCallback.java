package com.ccclubs.zble.scan;


import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.os.Handler;
import android.os.Looper;

import com.ccclubs.zble.RxBluetooth;


/**
 *RxPeriodScanCallback
 *
 * @author LiuLiWei
 */
public abstract class RxPeriodScanCallback implements LeScanCallback {
  protected Handler handler = new Handler(Looper.getMainLooper());
  protected long timeoutMillis;
  protected RxBluetooth rxBluetooth;

  public RxPeriodScanCallback(long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
  }

  public abstract void onScanTimeout();

  public void notifyScanStarted() {
    if(this.timeoutMillis > 0L) {
      this.removeHandlerMsg();
      this.handler.postDelayed(new Runnable() {
        public void run() {
          RxPeriodScanCallback.this.rxBluetooth.stopScan(RxPeriodScanCallback.this);
          RxPeriodScanCallback.this.onScanTimeout();
        }
      }, this.timeoutMillis);
    }

  }

  public void removeHandlerMsg() {
    this.handler.removeCallbacksAndMessages((Object)null);
  }

  public long getTimeoutMillis() {
    return this.timeoutMillis;
  }

  public RxPeriodScanCallback setTimeoutMillis(long timeoutMillis) {
    this.timeoutMillis = timeoutMillis;
    return this;
  }

  public RxBluetooth getRxBluetooth() {
    return this.rxBluetooth;
  }

  public RxPeriodScanCallback setRxBluetooth(RxBluetooth rxBluetooth) {
    this.rxBluetooth = rxBluetooth;
    return this;
  }
}
