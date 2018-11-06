package com.ccclubs.zble.exception;
/**
 *CharacteristicNotFoundException
 *
 * @author LiuLiWei
 */
public class CharacteristicNotFoundException extends RxBleException{
  public CharacteristicNotFoundException(String str) {
    super(201,str);
  }
}
