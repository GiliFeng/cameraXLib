# cameraLib
camerax身份证拍照
## 接入指南
### 一.接入
1.在build.gradle的allprojects中添加 <br>
```java
allprojects { 
	repositories { 
	...
	maven { url 'https://jitpack.io' }
}
}
```
2.在build.gradle中添加依赖 <br>
```java
dependencies {
	implementation 'com.github.GiliFeng:cameraLib:1.03'
}
```
### 二.使用
```java
 new CameraStart(MainActivity.this, new CameraEndCallBack() { 
                    @Override 
                    public void cameraEnd(File outFile) { 
                        ImageView imageView = findViewById(R.id.photo); 
                        imageView.setImageURI(Uri.fromFile(outFile)); 
                    } 
                }); 
```
### 三.添加权限  存储权限以及照相权限
```java
 <uses-permission android:name="android.permission.CAMERA" /> 
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> 
 ```
