package com.ccclubs.zble.exception.handler;


import android.content.Context;
import android.widget.Toast;

import com.ccclubs.zble.exception.ConnectException;
import com.ccclubs.zble.exception.GattException;
import com.ccclubs.zble.exception.InitiatedException;
import com.ccclubs.zble.exception.OtherException;
import com.ccclubs.zble.exception.TimeoutException;

/**
 *RxDefaultBleExceptionHandler
 *
 *@author LiuLiWei
 */
public class RxDefaultBleExceptionHandler extends RxBleExceptionHandler {
  private Context context;

  public RxDefaultBleExceptionHandler(Context context) {
    this.context = context.getApplicationContext();
  }

  protected void onConnectException(ConnectException e) {
    Toast.makeText(this.context, e.getDescription(), Toast.LENGTH_LONG).show();
  }

  protected void onGattException(GattException e) {
    Toast.makeText(this.context, e.getDescription(), Toast.LENGTH_LONG).show();
  }

  protected void onTimeoutException(TimeoutException e) {
    Toast.makeText(this.context, e.getDescription(), Toast.LENGTH_LONG).show();
  }

  protected void onInitiatedException(InitiatedException e) {
    Toast.makeText(this.context, e.getDescription(), Toast.LENGTH_LONG).show();
  }

  protected void onOtherException(OtherException e) {
    Toast.makeText(this.context, e.getDescription(), Toast.LENGTH_LONG).show();
  }
}
