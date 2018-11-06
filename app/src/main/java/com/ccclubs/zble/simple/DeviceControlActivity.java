package com.ccclubs.zble.simple;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ccclubs.zble.RxBleGattCallback;
import com.ccclubs.zble.conn.RxBleCharaCallback;
import com.ccclubs.zble.conn.RxBleConnector;
import com.ccclubs.zble.exception.RxBleException;
import com.ccclubs.zble.exception.handler.RxDefaultBleExceptionHandler;
import com.ccclubs.zble.utils.DataDecodeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * Control
 * <p>
 * Created by LiuLiWei on 2016/7/13 0013.
 */
public class DeviceControlActivity extends AppCompatActivity {

    private static final String TAG = "DeviceControlActivity";

    private TextView mConnectionState;

    private TextView mDataField;

    private ExpandableListView mGattServicesList;

    public static final String EXTRAS_RANDOM_DATA = "RANDOM_DATA";

    public static final String EXTRAS_DEVICE = "DEVICE";

    private final String LIST_NAME = "NAME";

    private final String LIST_UUID = "UUID";


    public String UUID_COMMAND_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";

    public String UUID_COMMAND_CHARA = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    private RxBleConnector mConnector;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<>();

    private boolean mConnected = false;

    private RxDefaultBleExceptionHandler mRxBleExceptionHandler;

    private BluetoothDevice mDevice;

    private Handler mHandler = new Handler();

    private RxBleGattCallback rxBleGattCallback = new RxBleGattCallback() {
        @Override
        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            mConnected = true;
            updateConnectionState(R.string.connected);
            invalidateOptionsMenu();
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        displayGattServices(gatt.getServices());
                    }
                }, 500);
            } else {
                Log.d(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onConnectFailure(RxBleException exception) {
            mConnected = false;
            updateConnectionState(R.string.disconnected);
            invalidateOptionsMenu();
            clearUI();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          final BluetoothGattCharacteristic characteristic,
                                          int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            displayData(DataDecodeUtil.decodeByteToHexString(characteristic.getValue()));
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        final Intent intent = getIntent();
        mDevice = intent.getParcelableExtra(EXTRAS_DEVICE);
        ((TextView) findViewById(R.id.device_address)).setText(mDevice.getAddress());
        mGattServicesList = findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListener);
        mConnectionState = findViewById(R.id.connection_state);
        mDataField = findViewById(R.id.data_value);
        mRxBleExceptionHandler = new RxDefaultBleExceptionHandler(this);
        mConnectionState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DeviceScanActivity.mRxBluetooth.connect(mDevice, false, rxBleGattCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                DeviceScanActivity.mRxBluetooth.connect(mDevice, false, rxBleGattCallback);
                return true;
            case R.id.menu_disconnect:
                DeviceScanActivity.mRxBluetooth.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param data
     */
    private void displayData(final String data) {
        if (!TextUtils.isEmpty(data)) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDataField.setText(data);
                }
            }, 500);
        }
    }

    /**
     *
     */
    private void clearUI() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
            }
        }, 500);
        mDataField.setText(R.string.no_data);
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (DeviceScanActivity.mRxBluetooth.isConnectingOrConnected()) {
            DeviceScanActivity.mRxBluetooth.disconnect();
            DeviceScanActivity.mRxBluetooth.closeBluetoothGatt();
        }
    }

    /**
     * @param resourceId
     */
    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();
        mGattCharacteristics = new ArrayList<>();
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, GattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, GattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
        SimpleExpandableListAdapter gattServiceAdapter =
                new SimpleExpandableListAdapter(this, gattServiceData,
                        android.R.layout.simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID},
                        new int[]{android.R.id.text1, android.R.id.text2}, gattCharacteristicData,
                        android.R.layout.simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID},
                        new int[]{android.R.id.text1, android.R.id.text2});
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    private final ExpandableListView.OnChildClickListener servicesListClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                    int childPosition, long id) {
            Log.d(TAG, "child click:" + "group position:" + groupPosition + ",child position:" + childPosition + ",id:" + id);
            if (mGattCharacteristics != null && mGattCharacteristics.size() > 0) {
                BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                if (characteristic != null) {
                    final int charaProp = characteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                        //操作
                    }
                }
                return true;
            }
            return false;
        }
    };
}
