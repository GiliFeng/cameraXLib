# cameraLib
camerax身份证拍照
## 接入指南
### 一.接入
1.allprojects { <br>
	repositories { <br>
	...<br>
	maven { url 'https://jitpack.io' }<br>
}<br>
}<br>
2.dependencies {<br>
	implementation 'com.github.GiliFeng:cameraLib:1.00' <br>
} <br>
### 二.使用
1.传入照片的名称
String outputPath = "/kin_" + System.currentTimeMillis() + ".jpg";
Intent intent = new Intent(MainActivity.this, CameraActivity.class);
intent.putExtra(KEY_OUTPUT_FILE_PATH,outputPath);
startActivityForResult(intent,  1001);
2.传回的imagePath
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                ImageView imageView = findViewById(R.id.photo);
                Bundle bundle = data.getExtras();  //data为B中回传的Intent
                File file = (File) bundle.get(KEY_OUTPUT_FILE_PATH);//str即为回传的值
                imageView.setImageURI(Uri.fromFile(file));
                break;
            default:
                break;
        }
}
3.添加权限  存储权限以及照相权限
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
