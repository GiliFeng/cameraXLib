package com.kin.cameralib;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.kin.cameralib.R;
import com.kin.cameralib.camera.CameraView;
import com.kin.cameralib.camera.KCameraLayout;
import com.kin.cameralib.camera.MaskView;
import com.kin.cameralib.camera.util.HideNavBarUtil;
import com.kin.cameralib.camera.util.PermissionCallback;
import com.kin.cameralib.camera.util.PicSaveUtil;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    public static final String KEY_OUTPUT_FILE_PATH = "outputFilePath";
    public static final String KEY_CONTENT_TYPE = "contentType";

    public static final String CONTENT_TYPE_GENERAL = "general";
    public static final String CONTENT_TYPE_ID_CARD_FRONT = "IDCardFront";
    public static final String CONTENT_TYPE_ID_CARD_BACK = "IDCardBack";
    public static final String CONTENT_TYPE_BANK_CARD = "bankCard";

    private static final int REQUEST_CODE_PICK_IMAGE = 100;
    private static final int PERMISSIONS_REQUEST_CAMERA = 800;
    private static final int PERMISSIONS_EXTERNAL_STORAGE = 801;

    private File outputFile;
    private String outputPath;
    private String contentType;
    private Handler handler = new Handler();

    private KCameraLayout takePictureContainer;
    private KCameraLayout confirmResultContainer;
    private CameraView cameraView;
    private ImageView displayImageView;
    private PermissionCallback permissionCallback = new PermissionCallback() {
        @Override
        public boolean onRequestPermission() {
            ActivityCompat.requestPermissions(CameraActivity.this,
                    new String[] {Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
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

        // confirm result;
        displayImageView = (ImageView) findViewById(R.id.display_image_view);
        confirmResultContainer.findViewById(R.id.confirm_button).setOnClickListener(confirmButtonOnClickListener);
        confirmResultContainer.findViewById(R.id.cancel_button).setOnClickListener(confirmCancelButtonOnClickListener);

        setOrientation(getResources().getConfiguration());
        initParams();
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
        contentType = getIntent().getStringExtra(KEY_CONTENT_TYPE);
        if (contentType == null) {
            contentType = CONTENT_TYPE_ID_CARD_FRONT;
        }
        int maskType;
        switch (contentType) {
            case CONTENT_TYPE_ID_CARD_FRONT:
                maskType = MaskView.MASK_TYPE_ID_CARD_FRONT;
                break;
            case CONTENT_TYPE_ID_CARD_BACK:
                maskType = MaskView.MASK_TYPE_ID_CARD_BACK;
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

    private CameraView.OnTakePictureCallback takePictureCallback = new CameraView.OnTakePictureCallback() {
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
                    String[] permissions ={
                            Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //验证是否许可权限
                    for (String str : permissions) {
                        if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                            //申请权限
                            requestPermissions(permissions, REQUEST_CODE_CONTACT);
                            Toast.makeText(getApplicationContext(), R.string.content_permission, Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }else{
                            PicSaveUtil.saveImageToGallery(getApplicationContext(),bitmap,outputFile,outputPath);
                        }
                    }
                }
                Intent intent = new Intent();
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, contentType);
                intent.putExtra(KEY_OUTPUT_FILE_PATH,outputFile);
                setResult(Activity.RESULT_OK, intent);
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
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);//休眠3秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                closeButtonOnClickListener.onClick(null);
            }
        });
    }
}