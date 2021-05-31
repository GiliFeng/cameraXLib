package com.kin.cameralib;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.kin.cameralib.camera.util.CameraEndCallBack;

import java.io.File;

/***
 *Created by wu on 2021/5/17
 **/
public class CameraStart {
    private static CameraEndCallBack cameraEndCallBack;
    /***
     * 返回的image File
     * **/
    public static final int RESULT_OK=Activity.RESULT_OK;
    public static final int RESULT_FAIL=-1001;
    /***
     * 选择拍照类型
     * */
    public enum CONTENT_CAMERA_TYPE {
        CONTENT_TYPE_ID_CARD_FRONT,/**身份证正面***/
        CONTENT_TYPE_ID_CARD_BACK,/**身份证背面***/
        CONTENT_TYPE_PERSON,/**人脸识别***/
    }
    public static void start(Activity context,CONTENT_CAMERA_TYPE camera_type,CameraEndCallBack cameraEndCallbacks){
        if (camera_type== CONTENT_CAMERA_TYPE.CONTENT_TYPE_ID_CARD_FRONT||camera_type== CONTENT_CAMERA_TYPE.CONTENT_TYPE_ID_CARD_BACK){
            startHorizonal(context, camera_type, cameraEndCallbacks);
        }else if (camera_type== CONTENT_CAMERA_TYPE.CONTENT_TYPE_PERSON){
            startVertical(context, camera_type, cameraEndCallbacks);
        }
    }

    /***横屏，身份证正面，身份证背面***/
    public static void startHorizonal(Activity context,CONTENT_CAMERA_TYPE camera_type,CameraEndCallBack cameraEndCallbacks){
        CameraActivity.newInstance(context,camera_type, new CameraEndCallBack() {
            @Override
            public void cameraEnd(int resultCode, File outFile) {
                if (RESULT_FAIL==resultCode||outFile==null){
                    Toast.makeText(context, R.string.get_empty_data, Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (resultCode==RESULT_OK){
                    context.runOnUiThread(new Runnable() {/***跳转到主线程，防止崩溃****/
                    @Override
                    public void run() {
                        CameraStart.cameraEndCallBack.cameraEnd(resultCode,outFile);
                    }
                    });
                }
            }
        });
        CameraStart.cameraEndCallBack=cameraEndCallbacks;
    }
    /***竖屏，人脸识别***/
    public static void startVertical(Activity context,CONTENT_CAMERA_TYPE camera_type,CameraEndCallBack cameraEndCallbacks){
        CameraVerticalActivity.newInstance(context,camera_type, new CameraEndCallBack() {
            @Override
            public void cameraEnd(int resultCode, File outFile) {
                if (RESULT_FAIL==resultCode||outFile==null){
                    Toast.makeText(context, R.string.get_empty_data, Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                if (resultCode==RESULT_OK){
                    context.runOnUiThread(new Runnable() {/***跳转到主线程，防止崩溃****/
                    @Override
                    public void run() {
                        CameraStart.cameraEndCallBack.cameraEnd(resultCode,outFile);
                    }
                    });
                }
            }
        });
        CameraStart.cameraEndCallBack=cameraEndCallbacks;
    }
}
