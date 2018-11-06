package com.ccclubs.zble.utils;

import android.bluetooth.BluetoothGattCharacteristic;

/**
 *DataDecodeUtil
 *
 * @author LiuLiWei
 */
public class DataDecodeUtil {

    /**
     *
     * @param characteristic
     * @return
     */
    public static String decodeByteToHexString(BluetoothGattCharacteristic characteristic) {
        //final byte[] data = characteristic.getValue();
        return decodeByteToHexString(characteristic.getValue());
    }

    /**
     *
     * @param data
     * @return
     */
    public static String decodeByteToHexString(byte[] data) {
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data) {
                stringBuilder.append(String.format("%02X", byteChar));
            }
//            String strData = new String(data) + "\n" + stringBuilder.toString();
            String strData = stringBuilder.toString();
            return strData;
        }
        return null;
    }
}
