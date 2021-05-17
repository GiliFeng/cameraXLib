# cameraLib
camerax身份证拍照
## 接入指南
### 一.接入
1.在build.gradle的allprojects中添加 <br>
allprojects { <br>
	repositories { <br>
	...<br>
	maven { url 'https://jitpack.io' }<br>
}<br>
}<br>
2.在build.gradle中添加依赖 <br>
dependencies {<br>
	implementation 'com.github.GiliFeng:cameraLib:1.03' <br>
} <br>
### 二.使用
 new CameraStart(MainActivity.this, new CameraEndCallBack() { <br>
                    @Override <br>
                    public void cameraEnd(File outFile) { <br>
                        ImageView imageView = findViewById(R.id.photo); <br>
                        imageView.setImageURI(Uri.fromFile(outFile)); <br>
                    } <br>
                }); <br>
### 三.添加权限  存储权限以及照相权限
 \<uses-permission android:name="android.permission.CAMERA" /> <br>
 \<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> <br>
