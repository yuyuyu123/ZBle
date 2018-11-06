package com.ccclubs.zble.simple;

import java.util.HashMap;

/**
 *
 *
 * Created by LiuLiWei on 2016/7/6 0006.
 */
public class GattAttributes {

  private static HashMap<String, String> attributes = new HashMap();
  public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
  public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

  static {
    // Sample Services.
    attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
   // attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
    attributes.put("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "Device Information Service");
    // Sample Characteristics.
    attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
    attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
  }

  public static String lookup(String uuid, String defaultName) {
    String name = attributes.get(uuid);
    return name == null ? defaultName : name;
  }
}
