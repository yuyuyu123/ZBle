package com.ccclubs.zble.simple;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ccclubs.zble.RxBluetooth;
import com.ccclubs.zble.scan.RxNameScanCallback;
import com.ccclubs.zble.scan.RxPeriodMacScanCallback;
import com.ccclubs.zble.scan.RxPeriodScanCallback;
import com.ccclubs.zble.utils.HexUtil;

/**
 * Scan
 * <p>
 * Created by LiuLiWei on 2016/7/13 0013.
 */
public class DeviceScanActivity extends AppCompatActivity {

    private ListView mListView;

    private LeDeviceListAdapter mLeDeviceListAdapter;

    private boolean mScanning;

    private Handler mHandler;

    public static RxBluetooth mRxBluetooth;

    private static int TIME_OUT_SCAN = 10000;

    private static final int REQUEST_ENABLE_BT = 1;

    private static final long SCAN_PERIOD = 10 * 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mListView = (ListView) findViewById(R.id.id_lv);

        mHandler = new Handler();
        /**检查当前设备是否支持Ble*/
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        /**初始化蓝牙信息*/
        if (mRxBluetooth == null) {
            mRxBluetooth = new RxBluetooth(this);
        }

        /**检查蓝牙初始化是否成功*/
        if (mRxBluetooth.getBluetoothAdapter() == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //蓝牙在没有打开的情况下请求打开蓝牙
        if (!mRxBluetooth.isEnabledBluetooth()) {
            mRxBluetooth.enableBluetooth(this, REQUEST_ENABLE_BT);
        }

        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        mListView.setAdapter(mLeDeviceListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
                if (device == null) return;
                final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
                intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE, device);
                if (mScanning) {
                    mRxBluetooth.stopScan(rxPeriodScanCallback);
                    //  mRxBluetooth.stopScan(rxNameScanCallback);
                    // mRxBluetooth.stopScan(rxPeriodMacScanCallback);
                    mScanning = false;
                }
                startActivity(intent);
            }
        });
        scanDevicesPeriod(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanDevicesPeriod(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(R.layout.indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanDevicesPeriod(true);
                break;
            case R.id.menu_stop:
                scanDevicesPeriod(false);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
    }

    /**
     * 打开或关闭周期性扫描
     *
     * @param ifScan 是否进行扫描
     */
    private void scanDevicesPeriod(boolean ifScan) {
        if (ifScan) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mRxBluetooth.stopScan(rxPeriodScanCallback);
                    // mRxBluetooth.stopScan(rxNameScanCallback);
                    //  mRxBluetooth.stopScan(rxPeriodMacScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);
            mScanning = true;
            //周期性扫描
            mRxBluetooth.startLeScan(rxPeriodScanCallback);
            //根据名字进行扫描
            // mRxBluetooth.startLeScan(rxNameScanCallback);
            //根据mac地址进行扫描
            // mRxBluetooth.startLeScan(rxPeriodMacScanCallback);
        } else {
            mScanning = false;
            mRxBluetooth.stopScan(rxPeriodScanCallback);
            //  mRxBluetooth.stopScan(rxNameScanCallback);
            //   mRxBluetooth.stopScan(rxPeriodMacScanCallback);
        }
        invalidateOptionsMenu();
    }

    /**
     * 扫描回调接口
     */
    private RxPeriodScanCallback rxPeriodScanCallback = new RxPeriodScanCallback(TIME_OUT_SCAN) {
        @Override
        public void onScanTimeout() {

        }

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };


    /**
     * Filter the unique device for the car
     *
     * @param random        random Str
     * @param macReal       real mac address from server
     * @param macStrVirtual the scanning device's virtual address
     * @return if our device
     */
    public boolean verifyDevice(byte[] random, byte[] macReal, String macStrVirtual) {
        byte[] macVirtual = new byte[6];
        int i, j;
        byte temp;
        for (i = 0; i < 6; i++) {
            temp = macReal[i];
            for (j = 0; j < 4; j++) {
                temp = (byte) (temp ^ random[j]);
            }
            macVirtual[5 - i] = temp;
        }
        macVirtual[0] = (byte) 0xc0;
        String resultVirtual = HexUtil.encodeHexStr(macVirtual).toUpperCase();
        String str1 = resultVirtual.substring(2);
        String str2 = macStrVirtual.substring(2);
        if (str1.equals(str2)) {
            return true;
        }
        return false;
    }

    /**
     * 根据Mac地址进制扫描
     */
    private RxPeriodScanCallback rxPeriodMacScanCallback = new RxPeriodScanCallback(TIME_OUT_SCAN) {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onScanTimeout() {

        }
    };


    /**
     * 根据名字进行扫描
     */
    private RxNameScanCallback rxNameScanCallback = new RxNameScanCallback("TESTBLUE", SCAN_PERIOD) {
        @Override
        public void onDeviceFound(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onScanTimeout() {
        }
    };

    /**
     * 获取随机数
     *
     * @param scanRecord
     * @return
     */
    private String getRandomString(byte[] scanRecord) {
        String subStr = HexUtil.encodeHexStr(scanRecord).substring(0,
                HexUtil.encodeHexStr(scanRecord).indexOf("0000"));
        String random = subStr.substring(34, 42);
        return random.toUpperCase();
    }

}
