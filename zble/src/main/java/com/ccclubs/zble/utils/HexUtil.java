package com.ccclubs.zble.utils;

import android.support.annotation.NonNull;

/**
 *HexUtil
 *
 * @author LiuLiWei
 */
public class HexUtil {
  private static final char[] DIGITS_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
  private static final char[] DIGITS_UPPER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  public HexUtil() {
  }

  public static char[] encodeHex(byte[] data) {
    return encodeHex(data, true);
  }

  public static char[] encodeHex(@NonNull byte[] data, boolean toLowerCase) {
    return encodeHex(data, toLowerCase?DIGITS_LOWER:DIGITS_UPPER);
  }

  protected static char[] encodeHex(@NonNull byte[] data, char[] toDigits) {
    int l = data.length;
    char[] out = new char[l << 1];
    int i = 0;

    for(int j = 0; i < l; ++i) {
      out[j++] = toDigits[(240 & data[i]) >>> 4];
      out[j++] = toDigits[15 & data[i]];
    }

    return out;
  }

  public static String encodeHexStr(@NonNull byte[] data) {
    return encodeHexStr(data, true);
  }

  public static String encodeHexStr(@NonNull byte[] data, boolean toLowerCase) {
    return encodeHexStr(data, toLowerCase?DIGITS_LOWER:DIGITS_UPPER);
  }

  protected static String encodeHexStr(@NonNull byte[] data, char[] toDigits) {
    return new String(encodeHex(data, toDigits));
  }

  public static byte[] decodeHex(@NonNull char[] data) {
    int len = data.length;
    if((len & 1) != 0) {
      throw new RuntimeException("Odd number of characters.");
    } else {
      byte[] out = new byte[len >> 1];
      int i = 0;

      for(int j = 0; j < len; ++i) {
        int f = toDigit(data[j], j) << 4;
        ++j;
        f |= toDigit(data[j], j);
        ++j;
        out[i] = (byte)(f & 255);
      }

      return out;
    }
  }

  protected static int toDigit(@NonNull char ch, int index) {
    int digit = Character.digit(ch, 16);
    if(digit == -1) {
      throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
    } else {
      return digit;
    }
  }

}
