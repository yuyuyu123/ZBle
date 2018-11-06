package com.ccclubs.zble.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *AESUtil
 *
 * @author LiuLiWei
 */
public class AESUtil {

  public AESUtil() {}

  /**
   * Encrypt by AES
   *
   * @param content need to be encrypted
   * @param password  password
   * @return  byte[]
   */
  public static byte[] encrypt(String content, String password) {
    try {
      SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
      byte[] byteContent = content.getBytes("utf-8");
      cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
      byte[] result = cipher.doFinal(byteContent);
      return result; // 加密
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Encrypt by AES
   *
   * @param byteContent need to be encrypted(byte[])
   * @param password password
     * @return
     */
  public static byte[] encrypt(byte[] byteContent, String password) {
    try {
      SecretKeySpec key = new SecretKeySpec(password.getBytes(), "AES");
      Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
      byte[] result = cipher.doFinal(byteContent);
      return result; // 加密
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
    } catch (InvalidKeyException e) {
      e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
    } catch (BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
