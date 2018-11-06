package com.ccclubs.zble.log;

import android.util.Log;

/**
 *RxBleLog
 *
 * @author LiuLiWei
 */
public final class RxBleLog {
  public static boolean isPrint = true;
  private static String defaultTag = "Log";

  private RxBleLog() {
  }

  public static void setTag(String tag) {
    defaultTag = tag;
  }

  public static int i(Object o) {
    return isPrint && o != null?Log.i(defaultTag, o.toString()):-1;
  }

  public static int i(String m) {
    return isPrint && m != null?Log.i(defaultTag, m):-1;
  }

  public static int v(String tag, String msg) {
    return isPrint && msg != null?Log.v(tag, msg):-1;
  }

  public static int d(String tag, String msg) {
    return isPrint && msg != null?Log.d(tag, msg):-1;
  }

  public static int i(String tag, String msg) {
    return isPrint && msg != null?Log.i(tag, msg):-1;
  }

  public static int w(String tag, String msg) {
    return isPrint && msg != null?Log.w(tag, msg):-1;
  }

  public static int e(String tag, String msg) {
    return isPrint && msg != null?Log.e(tag, msg):-1;
  }

  public static int v(String tag, Object... msg) {
    return isPrint?Log.v(tag, getLogMessage(msg)):-1;
  }

  public static int d(String tag, Object... msg) {
    return isPrint?Log.d(tag, getLogMessage(msg)):-1;
  }

  public static int i(String tag, Object... msg) {
    return isPrint?Log.i(tag, getLogMessage(msg)):-1;
  }

  public static int w(String tag, Object... msg) {
    return isPrint?Log.w(tag, getLogMessage(msg)):-1;
  }

  public static int e(String tag, Object... msg) {
    return isPrint?Log.e(tag, getLogMessage(msg)):-1;
  }

  private static String getLogMessage(Object... msg) {
    if(msg != null && msg.length > 0) {
      StringBuilder sb = new StringBuilder();
      Object[] arr$ = msg;
      int len$ = msg.length;

      for(int i$ = 0; i$ < len$; ++i$) {
        Object s = arr$[i$];
        if(msg != null) {
          sb.append(s.toString());
        }
      }

      return sb.toString();
    } else {
      return "";
    }
  }

  public static int v(String tag, String msg, Throwable tr) {
    return isPrint && msg != null?Log.v(tag, msg, tr):-1;
  }

  public static int d(String tag, String msg, Throwable tr) {
    return isPrint && msg != null?Log.d(tag, msg, tr):-1;
  }

  public static int i(String tag, String msg, Throwable tr) {
    return isPrint && msg != null?Log.i(tag, msg, tr):-1;
  }

  public static int w(String tag, String msg, Throwable tr) {
    return isPrint && msg != null?Log.w(tag, msg, tr):-1;
  }

  public static int e(String tag, String msg, Throwable tr) {
    return isPrint && msg != null?Log.e(tag, msg, tr):-1;
  }

  public static int v(Object tag, String msg) {
    return isPrint?Log.v(tag.getClass().getSimpleName(), msg):-1;
  }

  public static int d(Object tag, String msg) {
    return isPrint?Log.d(tag.getClass().getSimpleName(), msg):-1;
  }

  public static int i(Object tag, String msg) {
    return isPrint?Log.i(tag.getClass().getSimpleName(), msg):-1;
  }

  public static int w(Object tag, String msg) {
    return isPrint?Log.w(tag.getClass().getSimpleName(), msg):-1;
  }

  public static int e(Object tag, String msg) {
    return isPrint?Log.e(tag.getClass().getSimpleName(), msg):-1;
  }
}
