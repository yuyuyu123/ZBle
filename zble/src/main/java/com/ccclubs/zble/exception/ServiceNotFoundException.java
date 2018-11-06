package com.ccclubs.zble.exception;

/**
 *ServiceNotFoundException
 *
 * @author LiuLiWei
 */
public class ServiceNotFoundException extends RxBleException{
  public ServiceNotFoundException(String str) {
    super(201,str);
  }
}
