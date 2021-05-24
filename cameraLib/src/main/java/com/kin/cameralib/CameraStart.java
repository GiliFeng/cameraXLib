package com.kin.cameralib;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kin.cameralib.camera.event.BaseEvent;
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

    public static void start(Activity context,CameraEndCallBack cameraEndCallbacks){
        CameraActivity.newInstance(context, new CameraEndCallBack() {
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
