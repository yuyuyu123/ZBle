package com.ccclubs.zble;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.ccclubs.zble.conn.RxBleConnector;
import com.ccclubs.zble.exception.ConnectException;
import com.ccclubs.zble.exception.RxBleException;
import com.ccclubs.zble.scan.RxNameScanCallback;
import com.ccclubs.zble.scan.RxPeriodMacScanCallback;
import com.ccclubs.zble.scan.RxPeriodScanCallback;
import com.ccclubs.zble.utils.BluetoothUtil;

import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * RxBluetooth
 *
 * @author LiuLiWei
 */
public class RxBluetooth {

    private static final String TAG = RxBluetooth.class.getSimpleName();

    public static final int DEFAULT_SCAN_TIME = 10 * 1000;

    public static final int DEFAULT_CONN_TIME = 10 * 1000;

    public static final int STATE_DISCONNECTED = 0;

    public static final int STATE_SCANNING = 1;

    public static final int STATE_CONNECTING = 2;

    public static final int STATE_CONNECTED = 3;

    public static final int STATE_SERVICES_DISCOVERED = 4;

    private int connectionState = STATE_DISCONNECTED;

    private Context context;

    private BluetoothManager bluetoothManager;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothGatt bluetoothGatt;

    private Handler handler = new Handler(Looper.getMainLooper());

    private Set<BluetoothGattCallback> callbackList = new LinkedHashSet();

    private RxBleGattCallback coreGattCallback = new RxBleGattCallback() {
        public void onConnectFailure(RxBleException exception) {
            RxBluetooth.this.bluetoothGatt = null;

            Iterator i$ = RxBluetooth.this.callbackList.iterator();
            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                if (call instanceof RxBleGattCallback) {
                    ((RxBleGattCallback) call).onConnectFailure(exception);
                }
            }
        }

        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            RxBluetooth.this.bluetoothGatt = gatt;
            Iterator i$ = RxBluetooth.this.callbackList.iterator();
            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                if (call instanceof RxBleGattCallback) {
                    ((RxBleGattCallback) call).onConnectSuccess(gatt, status);
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                RxBluetooth.this.connectionState = 3;
                this.onConnectSuccess(gatt, status);
                bluetoothGatt.discoverServices();
            } else if (newState == 0) {
                RxBluetooth.this.connectionState = 0;
                this.onConnectFailure(new ConnectException(gatt, status));
            } else if (newState == 1) {
                RxBluetooth.this.connectionState = 2;
            }
            Iterator i$ = RxBluetooth.this.callbackList.iterator();
            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onConnectionStateChange(gatt, status, newState);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            RxBluetooth.this.connectionState = 4;
            Iterator i$ = RxBluetooth.this.callbackList.iterator();
            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onServicesDiscovered(gatt, status);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                         int status) {

            Iterator i$ = RxBluetooth.this.callbackList.iterator();
            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onCharacteristicRead(gatt, characteristic, status);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Iterator i$ = RxBluetooth.this.callbackList.iterator();
            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onCharacteristicWrite(gatt, characteristic, status);
            }
            bluetoothGatt.readCharacteristic(characteristic);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Iterator i$ = RxBluetooth.this.callbackList.iterator();

            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onCharacteristicChanged(gatt, characteristic);
            }

        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                     int status) {
            Iterator i$ = RxBluetooth.this.callbackList.iterator();

            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onDescriptorRead(gatt, descriptor, status);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {
            Iterator i$ = RxBluetooth.this.callbackList.iterator();

            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onDescriptorWrite(gatt, descriptor, status);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Iterator i$ = RxBluetooth.this.callbackList.iterator();

            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onReliableWriteCompleted(gatt, status);
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Iterator i$ = RxBluetooth.this.callbackList.iterator();

            while (i$.hasNext()) {
                BluetoothGattCallback call = (BluetoothGattCallback) i$.next();
                call.onReadRemoteRssi(gatt, rssi, status);
            }
        }
    };

    /**
     * Constructor
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public RxBluetooth(@NonNull Context context) {
        this.context = context = context.getApplicationContext();
        this.bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        this.bluetoothAdapter = this.bluetoothManager.getAdapter();
    }

    /**
     * Get RxBleConnector
     */
    public RxBleConnector getRxBleConnector() {
        return new RxBleConnector(this);
    }

    /**
     * @return
     */
    public boolean isInScanning() {
        return this.connectionState == STATE_SCANNING;
    }

    /**
     * @return
     */
    public boolean isConnectingOrConnected() {
        return this.connectionState >= STATE_CONNECTING;
    }

    /**
     * @return
     */
    public boolean isConnected() {
        return this.connectionState >= STATE_CONNECTED;
    }

    /**
     * @return
     */
    public boolean isServiceDiscovered() {
        return this.connectionState == STATE_SERVICES_DISCOVERED;
    }

    /**
     * @param callback
     * @return
     */
    public synchronized boolean addGattCallback(@Nullable BluetoothGattCallback callback) {
        return this.callbackList.add(callback);
    }

    /**
     * @param callback
     * @return
     */
    public synchronized boolean addGattCallback(@Nullable RxBleGattCallback callback) {
        return this.callbackList.add(callback);
    }

    /**
     * @param callback
     * @return
     */
    public synchronized boolean removeGattCallback(@NonNull BluetoothGattCallback callback) {
        return this.callbackList.remove(callback);
    }

    /**
     *
     */
    public synchronized void removeAllGattCallback() {
        try {
            if (this.callbackList != null && this.callbackList.size() > STATE_DISCONNECTED) {
                for (BluetoothGattCallback callback : this.callbackList) {
                    this.callbackList.remove(callback);
                }

            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param callback
     * @return
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    public boolean startLeScan(@Nullable LeScanCallback callback) {
        boolean suc = this.bluetoothAdapter.startLeScan(callback);
        if (suc) {
            this.connectionState = STATE_SCANNING;
        }
        return suc;
    }

    /**
     * @param callback
     * @return
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    public boolean startLeScan(@Nullable RxPeriodScanCallback callback) {
        callback.setRxBluetooth(this).notifyScanStarted();
        boolean suc = this.bluetoothAdapter.startLeScan(callback);
        if (suc) {
            this.connectionState = STATE_SCANNING;
        } else {
            callback.removeHandlerMsg();
        }

        return suc;
    }

    /**
     * @param callback
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    public void startLeScan(@Nullable RxPeriodMacScanCallback callback) {
        this.startLeScan((RxPeriodScanCallback) callback);
    }

    /**
     * @param callback
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    public void startLeScan(@Nullable RxNameScanCallback callback) {
        this.startLeScan((RxPeriodScanCallback) callback);
    }

    /**
     * @param callback
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopScan(@Nullable LeScanCallback callback) {
        if (callback instanceof RxPeriodScanCallback) {
            ((RxPeriodScanCallback) callback).removeHandlerMsg();
        }
        this.bluetoothAdapter.stopLeScan(callback);
        if (this.connectionState == STATE_SCANNING) {
            this.connectionState = STATE_DISCONNECTED;
        }
    }

    /**
     * @param device
     * @param autoConnect
     * @param callback
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresPermission(value = Manifest.permission.BLUETOOTH)
    public synchronized BluetoothGatt connect(@NonNull BluetoothDevice device, boolean autoConnect,
                                              @Nullable RxBleGattCallback callback) {
        Log.i(TAG, "connect device："
                + device.getName()
                + " mac:"
                + device.getAddress()
                + " autoConnect ------> "
                + autoConnect);
        this.callbackList.add(callback);
        this.bluetoothGatt = device.connectGatt(this.context, autoConnect, this.coreGattCallback);
        return this.bluetoothGatt;
        // return device.connectGatt(this.context, autoConnect, this.coreGattCallback);
    }

    /**
     *
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnect() {
        if (this.bluetoothGatt != null) {
            this.bluetoothGatt.disconnect();
        }
    }

    /**
     * @param mac
     * @param autoConnect
     * @param callback
     * @return
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    public boolean scanAndConnect(@NonNull final String mac, final boolean autoConnect,
                                  final RxBleGattCallback callback) {
        if (mac != null && mac.split(":").length == 6) {
            this.startLeScan(new RxPeriodMacScanCallback(mac, 20000L) {
                public void onScanTimeout() {
                    if (callback != null) {
                        callback.onConnectFailure(RxBleException.TIMEOUT_EXCEPTION);
                    }
                }

                public void onDeviceFound(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    RxBluetooth.this.runOnMainThread(new Runnable() {
                        public void run() {
                           connectTo(device, autoConnect, callback);
                        }
                    });
                }
            });
            return true;
        } else {
            throw new IllegalArgumentException("Illegal MAC address of the device! ");
        }
    }
    @RequiresPermission(value = Manifest.permission.BLUETOOTH)
    private void connectTo(@NonNull BluetoothDevice device, boolean autoConnect,
                         @Nullable RxBleGattCallback callback) {
        RxBluetooth.this.connect(device, autoConnect, callback);
    }

    /**
     * @return
     */
    public boolean refreshDeviceCache() {
        try {
            Method e = BluetoothGatt.class.getMethod("refresh", new Class[0]);
            if (e != null) {
                boolean success =
                        ((Boolean) e.invoke(this.getBluetoothGatt(), new Object[0])).booleanValue();
                Log.i(TAG, "Refreshing result: " + success);
                return success;
            }
        } catch (Exception var3) {
            Log.e(TAG, "An exception occured while refreshing device", var3);
        }

        return false;
    }

    /**
     *
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void closeBluetoothGatt() {
        if (this.bluetoothGatt != null) {
            this.bluetoothGatt.disconnect();
            this.refreshDeviceCache();
            if (this.bluetoothGatt != null) {
                this.bluetoothGatt.close();
            }
            Log.i(TAG, "closed BluetoothGatt ");
        }
    }

    /**
     * @param activity
     * @param requestCode
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH)
    public void enableBluetoothIfDisabled(@NonNull Activity activity, int requestCode) {
        if (!this.bluetoothAdapter.isEnabled()) {
            BluetoothUtil.enableBluetooth(activity, requestCode);
        }
    }

    /**
     * @return
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * @param runnable
     */
    public void runOnMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            this.handler.post(runnable);
        }
    }

    /**
     * @param activity
     * @param requestCode
     */
    public void enableBluetooth(@NonNull Activity activity, int requestCode) {
        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     *
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    public void enableBluetooth() {
        this.bluetoothAdapter.enable();
    }

    /**
     *
     */
    @RequiresPermission(value = Manifest.permission.BLUETOOTH_ADMIN)
    public void disableBluetooth() {
        this.bluetoothAdapter.disable();
    }

    /**
     * @return
     */
    @CheckResult
    public Context getContext() {
        return this.context;
    }

    /**
     * @return
     */
    @CheckResult
    public BluetoothManager getBluetoothManager() {
        return this.bluetoothManager;
    }

    /**
     * @return
     */
    @CheckResult
    public BluetoothAdapter getBluetoothAdapter() {
        return this.bluetoothAdapter;
    }

    /**
     * @return
     */
    @CheckResult
    public BluetoothGatt getBluetoothGatt() {
        return this.bluetoothGatt;
    }

    /**
     * @return
     */
    @CheckResult
    public int getConnectionState() {
        return this.connectionState;
    }

    /**
     * @return
     */
    @CheckResult
    @RequiresPermission(value = Manifest.permission.BLUETOOTH)
    public boolean isEnabledBluetooth() {
        if (this.bluetoothAdapter != null) {
            return this.bluetoothAdapter.isEnabled();
        }
        return false;
    }
}
