# ZBle

[![](https://jitpack.io/v/yuyuyu123/ZBle.svg)](https://jitpack.io/#yuyuyu123/ZBle)
# Dependency
Step1:在根目录.build文件下添加 
```gradle
repositories {
  maven { url "https://jitpack.io" }
}
```
Step2：在具体项目.build目录下添加
```gradle
implemention 'com.github.yuyuyu123:ZBle:1.0.0'
```

# Needs Permission
```xml
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>   
<uses-permission android:name="android.permission.BLUETOOTH"/>  
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>  
```

