package com.ccclubs.zble.exception.handler;


import com.ccclubs.zble.exception.ConnectException;
import com.ccclubs.zble.exception.GattException;
import com.ccclubs.zble.exception.InitiatedException;
import com.ccclubs.zble.exception.OtherException;
import com.ccclubs.zble.exception.RxBleException;
import com.ccclubs.zble.exception.TimeoutException;

/**
 *RxBleExceptionHandler
 *
 * @author LiuLiWei
 */
public abstract class RxBleExceptionHandler {
  public RxBleExceptionHandler() {}

  public RxBleExceptionHandler handleException(RxBleException exception) {
    if(exception != null) {
      if(exception instanceof ConnectException) {
        this.onConnectException((ConnectException)exception);
      } else if(exception instanceof GattException) {
        this.onGattException((GattException)exception);
      } else if(exception instanceof TimeoutException) {
        this.onTimeoutException((TimeoutException)exception);
      } else if(exception instanceof InitiatedException) {
        this.onInitiatedException((InitiatedException)exception);
      } else {
        this.onOtherException((OtherException)exception);
      }
    }

    return this;
  }

  protected abstract void onConnectException(ConnectException var1);

  protected abstract void onGattException(GattException var1);

  protected abstract void onTimeoutException(TimeoutException var1);

  protected abstract void onInitiatedException(InitiatedException var1);

  protected abstract void onOtherException(OtherException var1);
}
