package com.kin.cameralib.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.media.Image;
import android.util.Log;
import android.util.Size;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;

import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.kin.cameralib.camera.util.BitmapUtil;
import com.kin.cameralib.camera.util.DimensionUtil;
import com.kin.cameralib.camera.util.ICameraControl;
import com.kin.cameralib.camera.util.PermissionCallback;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static android.content.ContentValues.TAG;
/***
 *Created by wu on 2021/4/30
 **/
public class CameraXControl implements ICameraControl {
    private Context context;
    private PermissionCallback permissionCallback;/**权限**/
    private OnTakePictureCallback onTakePictureCallback;/***拍照回调**/
    private PreviewView mPreviewView;/**预览图**/
    private int orientation;
    private Size preSize;
    public CameraSelector cameraSelect = CameraSelector.DEFAULT_FRONT_CAMERA;/**调整前后摄像头*/
    private Rect previewFrame = new Rect();
    ImageCapture imageCapture;
    ProcessCameraProvider cameraProvider;
    private boolean isPhoto=false;/**正在拍摄***/
    /**设置预览图的大小***/
    private int preViewWidth;
    private int preViewHeight;
    /**相机控制*/
    CameraControl mCameraControl;
    @Override
    public void start() {
        Logger.getLogger("拍照开始");
        openCamera();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void stop() {/**相机释放**/
        if (mCameraControl!=null){
            cameraProvider.unbindAll();
        }
    }

    @Override
    public void pause() {
        setFlashMode(FLASH_MODE_OFF);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void resume() {
        if (mCameraControl!=null) {
//            CameraX.unbindAll();
        }
        openCamera();
    }

    @Override
    public View getDisplayView() {
        return mPreviewView;
    }

    @Override
    public Rect getPreviewFrame() {
        previewFrame.left = 0;
        previewFrame.top = 0;
        previewFrame.right = mPreviewView.getWidth();
        previewFrame.bottom = mPreviewView.getHeight();
        return previewFrame;
    }

    @Override
    public void takePicture(OnTakePictureCallback callback) {
        this.onTakePictureCallback = callback;
        if (isPhoto==false) {
            takePhoto();
            isPhoto=true;
        }
    }

    @Override
    public void setPermissionCallback(PermissionCallback callback) {
        this.permissionCallback = callback;
    }

    @Override
    public void setDisplayOrientation(@CameraView.Orientation int displayOrientation) {
        this.orientation = displayOrientation;
        mPreviewView.requestLayout();
    }

    @Override
    public void refreshPermission() {/***权限同意后刷新**/
        openCamera();
    }

    @Override
    public void setFlashMode(int flashMode) {
        switch (imageCapture.getCaptureMode()) {
            case ImageCapture.FLASH_MODE_AUTO:
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                break;
            case ImageCapture.FLASH_MODE_ON:
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                break;
            case ImageCapture.FLASH_MODE_OFF:
                imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
                break;
        }
    }

    @Override
    public int getFlashMode() {
        return imageCapture.getFlashMode();
    }

    public CameraXControl(Context activity,CameraSelector cameraSelect) {
        this.context = activity;
        this.cameraSelect=cameraSelect;
        preViewWidth=DimensionUtil.getScreenWidth(context);
        preViewHeight=DimensionUtil.getScreenHeight(context);
        if (preViewWidth>preViewHeight){/**横屏**/
            preSize=new Size(1920,1080);
        }else{
            preSize=new Size(1080,1920);
        }
        mPreviewView=new PreviewView(context);
    }
    private void requestCameraPermission() {
        if (permissionCallback != null) {
            permissionCallback.onRequestPermission();
        }
    }

    private void openCamera(){
        // 6.0+的系统需要检查系统权限 。
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        // 进行相机画面预览之前，设置想要的实现模式
//        mPreviewView.setPreferredImplementationMode(PreviewView.ImplementationMode.SURFACE_VIEW);
        //初始化相机
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(context);
        try {
            cameraProvider  = listenableFuture.get();
            //绑定预览
            Preview mPreview = new Preview.Builder()
                    .setTargetResolution(preSize)
                    .build();
            mPreview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
            //构建图像分析用例
            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    .build();
            imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context),
                    new ImageAnalysis.Analyzer() {
                        @Override
                        public void analyze(@NonNull ImageProxy image) {
                            image.close();
                        }
                    }
            );
            Camera camera= cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelect, mPreview, imageAnalysis);
            mCameraControl = camera.getCameraControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPreviewView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e(TAG,"手动对焦");
                if (mPreviewView==null){
                    return false;
                }
                // TODO 对焦
                MeteringPointFactory pointFactory=mPreviewView.getMeteringPointFactory();
                MeteringPoint meteringPoint = pointFactory.createPoint(event.getX(),event.getY());
                FocusMeteringAction action = new FocusMeteringAction.Builder(meteringPoint, FocusMeteringAction.FLAG_AF)
                        // auto calling cancelFocusAndMetering in 3 seconds
                        .setAutoCancelDuration(3, TimeUnit.SECONDS)
                        .build();
                ListenableFuture future = mCameraControl.startFocusAndMetering(action);
                future.addListener(() -> {
                    try {
                        FocusMeteringResult result = (FocusMeteringResult) future.get();
                        if (result.isFocusSuccessful()) {
                            Log.e(TAG,"对焦成功");
                        } else {
                            Log.e(TAG,"对焦失败");
                        }
                    } catch (Exception e) {
                    }
                }, ContextCompat.getMainExecutor(context));
                return false;
            }
        });
    }
    private void takePhoto() {
        //重新绑定之前必须先取消绑定
        cameraProvider.unbindAll();
        // 构建图像捕获用例
        ImageCapture  imageCapture = new ImageCapture.Builder()
                //优化捕获速度，可能降低图片质量
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                //设置宽高比
                .setTargetResolution(preSize)
                //设置初始的旋转角度
                .build();
        this.imageCapture=imageCapture;
        cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelect, imageCapture);
        imageCapture.takePicture(ContextCompat.getMainExecutor(context), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Log.e(TAG,"初始照片"+preViewWidth+"---"+preViewHeight+"---"+ image.getWidth()+"---"+image.getHeight());
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                Bitmap bitmap= BitmapUtil.decodeFrameToBitmap(bytes,image.getWidth(),image.getHeight());
                image.close();
                onTakePictureCallback.onPictureTaken(bytes,bitmap);
                super.onCaptureSuccess(image);
                isPhoto=false;
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                Log.e(TAG,"exception.getMessage()");
            }
        });
    }
}

