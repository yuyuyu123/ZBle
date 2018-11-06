package com.ccclubs.zble.conn;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import com.ccclubs.zble.RxBluetooth;
import com.ccclubs.zble.exception.CharacteristicNotFoundException;
import com.ccclubs.zble.exception.GattException;
import com.ccclubs.zble.exception.InitiatedException;
import com.ccclubs.zble.exception.OtherException;
import com.ccclubs.zble.exception.RxBleException;
import com.ccclubs.zble.log.RxBleLog;
import com.ccclubs.zble.utils.HexUtil;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *RxBleConnector
 *
 *@author LiuLiWei
 */
public class RxBleConnector {
  private static final String TAG = RxBleConnector.class.getSimpleName();

  public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";

  public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

  public static final UUID UUID_HEART_RATE_MEASUREMENT;

  private static final int MSG_WRITE_CHA = 1;
  private static final int MSG_WRITE_DES = 2;
  private static final int MSG_READ_CHA = 3;
  private static final int MSG_READ_DES = 4;
  private static final int MSG_READ_RSSI = 5;
  private static final int MSG_NOTIFY_CHA = 6;
  private static final int MSG_NOTIFY_DES = 7;

  private BluetoothGatt bluetoothGatt;

  private BluetoothGattService service;

  private BluetoothGattCharacteristic characteristic;

  private BluetoothGattDescriptor descriptor;

  private RxBluetooth rxBluetooth;

  private int timeOutMillis;

  private Handler handler;

  public RxBleConnector(@NonNull RxBluetooth rxBluetooth) {
    this.timeOutMillis = 10 * 1000;
    this.handler = new MyHandlder();
    this.rxBluetooth = rxBluetooth;
    this.bluetoothGatt = rxBluetooth.getBluetoothGatt();
    this.handler = new Handler(Looper.getMainLooper());
  }

  public RxBleConnector(@NonNull RxBluetooth rxBluetooth, BluetoothGattService service,
      BluetoothGattCharacteristic characteristic, BluetoothGattDescriptor descriptor) {
    this(rxBluetooth);
    this.service = service;
    this.characteristic = characteristic;
    this.descriptor = descriptor;
  }

  public RxBleConnector(@NonNull RxBluetooth rxBluetooth, UUID serviceUUID, UUID charactUUID,
      UUID descriptorUUID) {
    this(rxBluetooth);
    this.withUUID(serviceUUID, charactUUID, descriptorUUID);
  }

  public RxBleConnector(RxBluetooth rxBluetooth, String serviceUUID, String charactUUID,
      String descriptorUUID) {
    this(rxBluetooth);
    this.withUUIDString(serviceUUID, charactUUID, descriptorUUID);
  }

  public RxBleConnector withUUID(UUID serviceUUID, UUID charactUUID, UUID descriptorUUID) {
    if (serviceUUID != null && this.bluetoothGatt != null) {
      this.service = this.bluetoothGatt.getService(serviceUUID);
    }

    if (this.service != null && charactUUID != null) {
      this.characteristic = this.service.getCharacteristic(charactUUID);
    }

    if (this.characteristic != null && descriptorUUID != null) {
      this.descriptor = this.characteristic.getDescriptor(descriptorUUID);
    }

    return this;
  }

  public RxBleConnector withUUIDString(String serviceUUID, String charactUUID,
      String descriptorUUID) {
    return this.withUUID(this.formUUID(serviceUUID), this.formUUID(charactUUID),
        this.formUUID(descriptorUUID));
  }

  private UUID formUUID(@NonNull String uuid) {
    return uuid == null ? null : UUID.fromString(uuid);
  }

  public boolean writeCharacteristic(@Nullable byte[] data, @Nullable RxBleCharaCallback bleCallback) {
    return this.writeCharacteristic(this.getCharacteristic(), data, bleCallback);
  }

  public boolean writeCharacteristic(@NonNull BluetoothGattCharacteristic characteristic, @Nullable byte[] data,
      RxBleCharaCallback bleCallback) {
    if (characteristic == null) {
      bleCallback.onFailure(new CharacteristicNotFoundException(
          "Cannot find BluetoothGattCharacteristic with the characteristic UUID"));
      return false;
    }

    if (RxBleLog.isPrint) {
      RxBleLog.i(TAG, characteristic.getUuid()
          + " characteristic write bytes: "
          + Arrays.toString(data)
          + " ,hex: "
          + HexUtil.encodeHexStr(data));
    }
    this.handleCharacteristicWriteCallback(bleCallback);
    characteristic.setValue(data);
    return this.handleAfterInitialed(this.getBluetoothGatt().writeCharacteristic(characteristic),
        bleCallback);
  }

  public boolean writeDescriptor(byte[] data, RxBleDescriptorCallback bleCallback) {
    return this.writeDescriptor(this.getDescriptor(), data, bleCallback);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  public boolean writeDescriptor(@NonNull BluetoothGattDescriptor descriptor, byte[] data,
      RxBleDescriptorCallback bleCallback) {
    if (RxBleLog.isPrint) {
      RxBleLog.i(TAG, descriptor.getUuid()
          + " descriptor write bytes: "
          + Arrays.toString(data)
          + " ,hex: "
          + HexUtil.encodeHexStr(data));
    }

    this.handleDescriptorWriteCallback(bleCallback);
    descriptor.setValue(data);
    return this.handleAfterInitialed(this.getBluetoothGatt().writeDescriptor(descriptor),
        bleCallback);
  }

  public boolean readCharacteristic(RxBleCharaCallback bleCallback) {
    return this.readCharacteristic(this.getCharacteristic(), bleCallback);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  public boolean readCharacteristic(@NonNull BluetoothGattCharacteristic characteristic,
      RxBleCharaCallback bleCallback) {
    if ((this.characteristic.getProperties() | 2) > 0) {
      this.setCharacteristicNotification(this.getBluetoothGatt(), characteristic, false);
     this.handleCharacteristicReadCallback(bleCallback);
      return this.handleAfterInitialed(this.getBluetoothGatt().readCharacteristic(characteristic),
          bleCallback);
    } else {
      if (bleCallback != null) {
        bleCallback.onFailure(new OtherException("Characteristic [is not] readable!"));
      }

      return false;
    }
  }

  public boolean readDescriptor(@NonNull RxBleDescriptorCallback bleCallback) {
    return this.readDescriptor(this.getDescriptor(), bleCallback);
  }

  public boolean readDescriptor(@NonNull BluetoothGattDescriptor descriptor,
      RxBleDescriptorCallback bleCallback) {
    this.handleDescriptorReadCallback(bleCallback);
    return this.handleAfterInitialed(this.getBluetoothGatt().readDescriptor(descriptor),
        bleCallback);
  }

  public boolean readRemoteRssi(@NonNull RxBleRssiCallback bleCallback) {
    this.handleRSSIReadCallback(bleCallback);
    return this.handleAfterInitialed(this.getBluetoothGatt().readRemoteRssi(), bleCallback);
  }

  public boolean enableCharacteristicNotification(RxBleCharaCallback bleCallback) {
    return this.enableCharacteristicNotification(this.getCharacteristic(), bleCallback);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  public boolean enableCharacteristicNotification(@NonNull BluetoothGattCharacteristic charact,
      RxBleCharaCallback bleCallback) {
    if ((charact.getProperties() | 16) > 0) {
      this.handleCharacteristicNotificationCallback(bleCallback);
      return this.setCharacteristicNotification(this.getBluetoothGatt(), charact, true);
    } else {
      if (bleCallback != null) {
        bleCallback.onFailure(new OtherException("Characteristic [not supports] readable!"));
      }

      return false;
    }
  }

  public boolean enableDescriptorNotification(@NonNull RxBleDescriptorCallback bleCallback) {
    return this.enableDescriptorNotification(this.getDescriptor(), bleCallback);
  }

  public boolean enableDescriptorNotification(@NonNull BluetoothGattDescriptor descriptor,
      RxBleDescriptorCallback bleCallback) {
    this.handleDescriptorNotificationCallback(bleCallback);
    return this.setDescriptorNotification(this.getBluetoothGatt(), descriptor, true);
  }

  private boolean handleAfterInitialed(boolean initiated, RxBleCallback bleCallback) {
    if (bleCallback != null) {
      if (initiated) {
        bleCallback.onInitiatedSuccess();
      } else {
        bleCallback.onFailure(new InitiatedException());
      }
    }

    return initiated;
  }

  public boolean setNotification(boolean enable) {
    return this.setNotification(this.getBluetoothGatt(), this.getCharacteristic(),
        this.getDescriptor(), enable);
  }

  public boolean setNotification(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic,
      BluetoothGattDescriptor descriptor, boolean enable) {
    return this.setCharacteristicNotification(gatt, characteristic, enable)
        && this.setDescriptorNotification(gatt, descriptor, enable);
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  public boolean setCharacteristicNotification(@NonNull BluetoothGatt gatt,
                                               @NonNull BluetoothGattCharacteristic characteristic, boolean enable) {
    if (gatt != null && characteristic != null) {
      RxBleLog.i(TAG, "Characteristic set notification value: " + enable);
      boolean success = gatt.setCharacteristicNotification(characteristic, enable);
      if(characteristic.getDescriptors().size() > 0) {
        for(BluetoothGattDescriptor descriptor:characteristic.getDescriptors()) {
          descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
          boolean b = gatt.writeDescriptor(descriptor);
        }
      }
      return success;
    } else {
      return false;
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  public boolean setDescriptorNotification(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattDescriptor descriptor,
      boolean enable) {
    if (gatt != null && descriptor != null) {
      RxBleLog.i(TAG, "Descriptor set notification value: " + enable);
      if (enable) {
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      } else {
        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
      }

      return gatt.writeDescriptor(descriptor);
    } else {
      return false;
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void handleCharacteristicWriteCallback(final RxBleCharaCallback bleCallback) {
    if (bleCallback != null) {
      this.listenAndTimer(bleCallback, MSG_WRITE_CHA, new BluetoothGattCallback() {
        public void onCharacteristicWrite(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic, int status) {
          RxBleConnector.this.handler.removeMessages(MSG_WRITE_CHA, this);
          if (status == 0) {
            bleCallback.onSuccess(characteristic);
          } else {
            bleCallback.onFailure(new GattException(status));
          }
          RxBleConnector.this.rxBluetooth.removeGattCallback(bleCallback.getBluetoothGattCallback());
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void handleDescriptorWriteCallback(final RxBleDescriptorCallback bleCallback) {
    if (bleCallback != null) {
      this.listenAndTimer(bleCallback, MSG_WRITE_DES, new BluetoothGattCallback() {
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
            int status) {
          RxBleConnector.this.handler.removeMessages(MSG_WRITE_DES, this);
          if (status == 0) {
            bleCallback.onSuccess(descriptor);
          } else {
            bleCallback.onFailure(new GattException(status));
          }
          RxBleConnector.this.rxBluetooth.removeGattCallback(bleCallback.getBluetoothGattCallback());
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void handleCharacteristicReadCallback(final RxBleCharaCallback bleCallback) {
    if (bleCallback != null) {
      this.listenAndTimer(bleCallback, MSG_READ_CHA, new BluetoothGattCallback() {

        AtomicBoolean msgRemoved = new AtomicBoolean(false);

        public void onCharacteristicRead(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic, int status) {
          if (!this.msgRemoved.getAndSet(true)) {
            RxBleConnector.this.handler.removeMessages(MSG_READ_CHA, this);
          }
          if (status == 0) {
            bleCallback.onSuccess(characteristic);
          } else {
            bleCallback.onFailure(new GattException(status));
          }
          RxBleConnector.this.rxBluetooth.removeGattCallback(bleCallback.getBluetoothGattCallback());
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void handleDescriptorReadCallback(final RxBleDescriptorCallback bleCallback) {
    if (bleCallback != null) {
      this.listenAndTimer(bleCallback, MSG_READ_DES, new BluetoothGattCallback() {
        AtomicBoolean msgRemoved = new AtomicBoolean(false);

        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
            int status) {
          if (!this.msgRemoved.getAndSet(true)) {
            RxBleConnector.this.handler.removeMessages(MSG_READ_DES, this);
          }

          if (status == 0) {
            bleCallback.onSuccess(descriptor);
          } else {
            bleCallback.onFailure(new GattException(status));
          }
          RxBleConnector.this.rxBluetooth.removeGattCallback(bleCallback.getBluetoothGattCallback());
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void handleRSSIReadCallback(final RxBleRssiCallback bleCallback) {
    if (bleCallback != null) {
      this.listenAndTimer(bleCallback, MSG_READ_RSSI, new BluetoothGattCallback() {
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
          RxBleConnector.this.handler.removeMessages(MSG_READ_RSSI, this);
          if (status == 0) {
            bleCallback.onSuccess(rssi);
          } else {
            bleCallback.onFailure(new GattException(status));
          }
          RxBleConnector.this.rxBluetooth.removeGattCallback(bleCallback.getBluetoothGattCallback());
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void handleCharacteristicNotificationCallback(final RxBleCharaCallback bleCallback) {
    if (bleCallback != null) {
      this.listenAndTimer(bleCallback, MSG_NOTIFY_CHA, new BluetoothGattCallback() {
        AtomicBoolean msgRemoved = new AtomicBoolean(false);

        public void onCharacteristicChanged(BluetoothGatt gatt,
            BluetoothGattCharacteristic characteristic) {
          if (!this.msgRemoved.getAndSet(true)) {
            RxBleConnector.this.handler.removeMessages(MSG_NOTIFY_CHA, this);
          }
          bleCallback.onSuccess(characteristic);
        }
      });
    }
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
  private void handleDescriptorNotificationCallback(final RxBleDescriptorCallback bleCallback) {
    if (bleCallback != null) {
      this.listenAndTimer(bleCallback, MSG_NOTIFY_DES, new BluetoothGattCallback() {
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
            int status) {
          RxBleConnector.this.handler.removeMessages(MSG_NOTIFY_DES, this);
          if (status == 0) {
            bleCallback.onSuccess(descriptor);
          } else {
            bleCallback.onFailure(new GattException(status));
          }
          RxBleConnector.this.rxBluetooth.removeGattCallback(bleCallback.getBluetoothGattCallback());
        }
      });
    }
  }

  private void listenAndTimer(RxBleCallback bleCallback, int what, BluetoothGattCallback callback) {
    bleCallback.setBluetoothGattCallback(callback);
    this.rxBluetooth.addGattCallback(callback);
    Message msg = this.handler.obtainMessage(what, bleCallback);
    this.handler.sendMessageDelayed(msg, (long) this.timeOutMillis);
  }

  public BluetoothGatt getBluetoothGatt() {
    return this.bluetoothGatt;
  }

  public RxBleConnector setBluetoothGatt(BluetoothGatt bluetoothGatt) {
    this.bluetoothGatt = bluetoothGatt;
    return this;
  }

  public BluetoothGattService getService() {
    return this.service;
  }

  public RxBleConnector setService(BluetoothGattService service) {
    this.service = service;
    return this;
  }

  public BluetoothGattCharacteristic getCharacteristic() {
    return this.characteristic;
  }

  public RxBleConnector setCharacteristic(BluetoothGattCharacteristic characteristic) {
    this.characteristic = characteristic;
    return this;
  }

  public BluetoothGattDescriptor getDescriptor() {
    return this.descriptor;
  }

  public RxBleConnector setDescriptor(BluetoothGattDescriptor descriptor) {
    this.descriptor = descriptor;
    return this;
  }

  public int getTimeOutMillis() {
    return this.timeOutMillis;
  }

  public RxBleConnector setTimeOutMillis(int timeOutMillis) {
    this.timeOutMillis = timeOutMillis;
    return this;
  }

  static {
    UUID_HEART_RATE_MEASUREMENT = UUID.fromString(HEART_RATE_MEASUREMENT);
  }

  private class MyHandlder extends Handler {
    private MyHandlder() {
    }

    public void handleMessage(Message msg) {
      RxBleCallback call = (RxBleCallback) msg.obj;
      if (call != null) {
        RxBleConnector.this.rxBluetooth.removeGattCallback(call.getBluetoothGattCallback());
        call.onFailure(RxBleException.TIMEOUT_EXCEPTION);
      }

      msg.obj = null;
    }
  }
}
