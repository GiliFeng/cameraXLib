package com.kin.cameralib.camera.util;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntDef;

import com.kin.cameralib.camera.CameraView;

/***
 *Created by wu on 2021/4/29
 **/
public interface ICameraControl {

    /**
     * 闪光灯关 {@link #setFlashMode(int)}
     */
    int FLASH_MODE_OFF = 0;
    /**
     * 闪光灯开 {@link #setFlashMode(int)}
     */
    int FLASH_MODE_TORCH = 1;
    /**
     * 闪光灯自动 {@link #setFlashMode(int)}
     */
    int FLASH_MODE_AUTO = 2;

    @IntDef({FLASH_MODE_TORCH, FLASH_MODE_OFF, FLASH_MODE_AUTO})
    @interface FlashMode {

    }

    /**
     * 照相回调。
     */
    interface OnTakePictureCallback {
        void onPictureTaken(byte[] data);
        void onPictureTaken(byte[] byteData, Bitmap bitData);
    }

    /**
     * 打开相机。
     */
    void start();

    /**
     * 关闭相机
     */
    void stop();

    void pause();

    void resume();

    /**
     * 相机对应的预览视图。
     * @return 预览视图
     */
    View getDisplayView();

    /**
     * 看到的预览可能不是照片的全部。返回预览视图的全貌。
     * @return 预览视图frame;
     */
    Rect getPreviewFrame();

    /**
     * 拍照。结果在回调中获取。
     * @param callback 拍照结果回调
     */
    void takePicture(OnTakePictureCallback callback);

    /**
     * 设置权限回调，当手机没有拍照权限时，可在回调中获取。
     * @param callback 权限回调
     */
    void setPermissionCallback(PermissionCallback callback);

    /**
     * 设置水平方向
     * @param displayOrientation 参数值见 {@link CameraView.Orientation}
     */
    void setDisplayOrientation(@CameraView.Orientation int displayOrientation);

    /**
     * 获取到拍照权限时，调用些函数以继续。
     */
    void refreshPermission();

    /**
     *  设置闪光灯状态。
     * @param flashMode {@link #FLASH_MODE_TORCH,#FLASH_MODE_OFF,#FLASH_MODE_AUTO}
     */
    void setFlashMode(@FlashMode int flashMode);

    /**
     * 获取当前闪光灯状态
     * @return 当前闪光灯状态 参见 {@link #setFlashMode(int)}
     */
    @FlashMode
    int getFlashMode();
}

