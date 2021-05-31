package com.kin.cameralib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kin.cameralib.camera.CameraView;
import com.kin.cameralib.camera.KCameraLayout;
import com.kin.cameralib.camera.MaskView;
import com.kin.cameralib.camera.util.BitmapUtil;
import com.kin.cameralib.camera.util.CameraEndCallBack;
import com.kin.cameralib.camera.util.HideNavBarUtil;
import com.kin.cameralib.camera.util.PermissionCallback;
import com.kin.cameralib.camera.util.PicSaveUtil;
import com.kin.cameralib.camera.util.PopWindowUtil;

import java.io.File;

import static com.kin.cameralib.CameraStart.CONTENT_CAMERA_TYPE.CONTENT_TYPE_ID_CARD_FRONT;

public class CameraVerticalActivity extends AppCompatActivity {
    public static final String KEY_OUTPUT_FILE_PATH = "outputFilePath";
    /***权限***/
    private static final int PERMISSIONS_REQUEST_CAMERA = 800;/***拍照**/
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 801;/***文件存储**/

    private File outputFile;
    private String outputPath;
    private Handler handler = new Handler();

    private KCameraLayout takePictureContainer;
    private KCameraLayout confirmResultContainer;
    private CameraView cameraView;
    private ImageView displayImageView;
    private PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public boolean onRequestPermission() {
            ActivityCompat.requestPermissions(CameraVerticalActivity.this,
                    new String[] {Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
    };
    /***初始化activity以及传值***/
    private static CameraEndCallBack cameraEndCallbacks;
    private static CameraStart.CONTENT_CAMERA_TYPE contentType;/***相机类型**/

    public static void newInstance(Context context, CameraStart.CONTENT_CAMERA_TYPE camera_type, CameraEndCallBack cameraEndCallbacks) {
        Intent starter = new Intent(context, CameraVerticalActivity.class);
        CameraVerticalActivity.cameraEndCallbacks=cameraEndCallbacks;
        CameraVerticalActivity.contentType=camera_type;
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_vertical);
        /**设置状态栏字体颜色**/
//        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null);
//        StatusBarUtil.setLightMode(this);
        /***隐藏虚拟键盘**/
        HideNavBarUtil.hideBottomUIMenu(getWindow().getDecorView());
        /**隐藏状态栏**/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        takePictureContainer = (KCameraLayout) findViewById(R.id.take_picture_container);
        confirmResultContainer = (KCameraLayout) findViewById(R.id.confirm_result_container);
        cameraView = (CameraView) findViewById(R.id.camera_view);
        cameraView.getCameraControl().setPermissionCallback(permissionCallback);
        findViewById(R.id.take_photo_button).setOnClickListener(takeButtonOnClickListener);
        findViewById(R.id.go_back_button).setOnClickListener(closeButtonOnClickListener);
        findViewById(R.id.toolbar_back_button).setOnClickListener(closeButtonOnClickListener);
        /**title信息***/
        TextView tv_title=findViewById(R.id.tv_take_picture_title);
        tv_title.setText(R.string.camera_title);
        // confirm result;
        displayImageView = (ImageView) findViewById(R.id.display_image_view);
        displayImageView.setBackgroundResource(R.color.white);
        confirmResultContainer.findViewById(R.id.confirm_button).setOnClickListener(confirmButtonOnClickListener);
        confirmResultContainer.findViewById(R.id.cancel_button).setOnClickListener(confirmCancelButtonOnClickListener);
        setOrientation(getResources().getConfiguration());
        initParams();
        showPopwindow();
    }
    private PopupWindow popupWindow;
    private void showPopwindow(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
             popupWindow =PopWindowUtil.showInfo(CameraVerticalActivity.this);
            }
        }, 500);//3秒后执行Runnable中的run方法
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(popupWindow!=null&&popupWindow.isShowing()){
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        cameraView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraView.stop();
    }

    private void initParams() {
//        String saveDir = Environment.getExternalStorageDirectory() + "/DCIM/camera/";
//        String saveDir =getFilesDir().getPath();
        File saveDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        outputPath = getIntent().getStringExtra(KEY_OUTPUT_FILE_PATH);
        if (TextUtils.isEmpty(outputPath)){/**设置默认**/
            outputPath = "/kin_" + System.currentTimeMillis() + ".jpg";
        }
        if (outputPath != null) {
            outputFile = new File(saveDir,outputPath);
        }
        int maskType;
        switch (contentType) {
            case CONTENT_TYPE_ID_CARD_FRONT:
                maskType = MaskView.MASK_TYPE_ID_CARD_FRONT;
                break;
            case CONTENT_TYPE_ID_CARD_BACK:
                maskType = MaskView.MASK_TYPE_ID_CARD_BACK;
                break;
            case CONTENT_TYPE_PERSON:
                maskType = MaskView.MASK_TYPE_ID_CARD_PERSON;
                break;
            default:
                maskType = MaskView.MASK_TYPE_NONE;
                break;
        }
        cameraView.setMaskType(maskType);
    }
    private void showTakePicture() {
        cameraView.getCameraControl().resume();
        takePictureContainer.setVisibility(View.VISIBLE);
        confirmResultContainer.setVisibility(View.INVISIBLE);
    }

    private void showResultConfirm() {
        cameraView.getCameraControl().pause();
        takePictureContainer.setVisibility(View.INVISIBLE);
        confirmResultContainer.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener takeButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cameraView.takePicture(outputFile, takePictureCallback);
        }
    };

    private final CameraView.OnTakePictureCallback takePictureCallback = new CameraView.OnTakePictureCallback() {
        @Override
        public void onPictureTaken(final Bitmap bitmap) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    takePictureContainer.setVisibility(View.INVISIBLE);
                    displayImageView.setImageBitmap(bitmap);
                    showResultConfirm();
                }
            });
        }
    };

    // confirm result;
    private View.OnClickListener closeButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(Activity.RESULT_CANCELED);
            cameraView.stop();
            cameraEndCallbacks.cameraEnd(-1001,null);
            finish();
        }
    };

    private void doConfirmResult() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = ((BitmapDrawable) displayImageView.getDrawable()).getBitmap();
                if (Build.VERSION.SDK_INT >= 23) {
                    int REQUEST_CODE_CONTACT = 101;
                    String[] permissions = {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //验证是否许可权限
                    for (String str : permissions) {
                        if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            requestPermissions(permissions, REQUEST_CODE_CONTACT);
                            Toast.makeText(getApplicationContext(), R.string.content_permission, Toast.LENGTH_LONG)
                                    .show();
                            return;
                        } else {
                            Boolean isComplete=PicSaveUtil.saveImageToGallery(getApplicationContext(), bitmap, outputFile, outputPath);
                            if (isComplete){/***图片保存到本地成功**/
                                cameraEndCallbacks.cameraEnd(Activity.RESULT_OK,outputFile);
                            }else{
                                Toast.makeText(CameraVerticalActivity.this, R.string.get_empty_data, Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }
                }
                finish();
            }
        }.start();
    }

    private View.OnClickListener confirmButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doConfirmResult();
        }
    };

    private View.OnClickListener confirmCancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayImageView.setImageBitmap(null);
            showTakePicture();
        }
    };

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setOrientation(newConfig);
    }

    private void setOrientation(Configuration newConfig) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int orientation;
        int cameraViewOrientation = CameraView.ORIENTATION_PORTRAIT;
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                cameraViewOrientation = CameraView.ORIENTATION_PORTRAIT;
                orientation = KCameraLayout.ORIENTATION_PORTRAIT;
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                orientation = KCameraLayout.ORIENTATION_HORIZONTAL;
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    cameraViewOrientation = CameraView.ORIENTATION_HORIZONTAL;
                } else {
                    cameraViewOrientation = CameraView.ORIENTATION_INVERT;
                }
                break;
            default:
                orientation = KCameraLayout.ORIENTATION_PORTRAIT;
                cameraView.setOrientation(CameraView.ORIENTATION_PORTRAIT);
                break;
        }
        takePictureContainer.setOrientation(orientation);
        cameraView.setOrientation(cameraViewOrientation);
        confirmResultContainer.setOrientation(orientation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraView.getCameraControl().refreshPermission();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.camera_permission, Toast.LENGTH_LONG)
                            .show();
                    stopWithoutPermission();
                }
                break;
            }
            case PERMISSIONS_EXTERNAL_STORAGE:
            default:
                break;
        }
    }
    private void stopWithoutPermission(){
        finish();
    }
}