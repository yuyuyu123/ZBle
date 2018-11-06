package com.ccclubs.zble.utils;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;

import com.ccclubs.zble.log.RxBleLog;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

/**
 *BluetoothUtil
 *
 * @author LiuLiWei
 */
public class BluetoothUtil {
  private static final String TAG = "BluetoothUtil";

  public BluetoothUtil() {
  }

  /**
   *
   * @param activity
   * @param requestCode
     */
  public static void enableBluetooth(Activity activity, int requestCode) {
    Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
    activity.startActivityForResult(intent, requestCode);
  }

  /**
   *
   * @param gatt
     */
  public static void printServices(BluetoothGatt gatt) {
    if(gatt != null) {
      Iterator i$ = gatt.getServices().iterator();

      while(i$.hasNext()) {
        BluetoothGattService service = (BluetoothGattService)i$.next();
        RxBleLog.i(TAG, "service: " + service.getUuid());
        Iterator i$1 = service.getCharacteristics().iterator();

        while(i$1.hasNext()) {
          BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic)i$1.next();
          RxBleLog.d(TAG, "  characteristic: " + characteristic.getUuid() + " value: " + Arrays.toString(characteristic.getValue()));
          Iterator i$2 = characteristic.getDescriptors().iterator();

          while(i$2.hasNext()) {
            BluetoothGattDescriptor descriptor = (BluetoothGattDescriptor)i$2.next();
            RxBleLog.v(TAG, "        descriptor: " + descriptor.getUuid() + " value: " + Arrays.toString(descriptor.getValue()));
          }
        }
      }
    }

  }

  /**
   *
   * @param gatt
   * @return
     */
  public static boolean refreshDeviceCache(BluetoothGatt gatt) {
    try {
      Method e = BluetoothGatt.class.getMethod("refresh", new Class[0]);
      if(e != null) {
        boolean success = ((Boolean)e.invoke(gatt, new Object[0])).booleanValue();
        Log.i("BluetoothUtil", "Refreshing result: " + success);
        return success;
      }
    } catch (Exception var3) {
      Log.e("BluetoothUtil", "An exception occured while refreshing device", var3);
    }

    return false;
  }

  /**
   *
   * @param gatt
     */
  public static void closeBluetoothGatt(BluetoothGatt gatt) {
    if(gatt != null) {
      gatt.disconnect();
      refreshDeviceCache(gatt);
      gatt.close();
    }

  }

  /**
   *
   * @param gatt
   * @param serviceUUID
   * @return
     */
  public static BluetoothGattService getService(BluetoothGatt gatt, String serviceUUID) {
    return gatt.getService(UUID.fromString(serviceUUID));
  }

  /**
   *
   * @param service
   * @param charactUUID
   * @return
     */
  public static BluetoothGattCharacteristic getCharacteristic(BluetoothGattService service, String charactUUID) {
    return service != null?service.getCharacteristic(UUID.fromString(charactUUID)):null;
  }

  /**
   *
   * @param gatt
   * @param serviceUUID
   * @param charactUUID
     * @return
     */
  public static BluetoothGattCharacteristic getCharacteristic(BluetoothGatt gatt, String serviceUUID, String charactUUID) {
    BluetoothGattService service = gatt.getService(UUID.fromString(serviceUUID));
    return service != null?service.getCharacteristic(UUID.fromString(charactUUID)):null;
  }
}
