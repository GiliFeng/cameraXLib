package com.kin.cameralib;

import android.app.Activity;
import android.content.Intent;

import com.kin.cameralib.camera.event.BaseEvent;
import com.kin.cameralib.camera.util.CameraEndCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/***
 *Created by wu on 2021/5/17
 **/
public class CameraStart {
    private CameraEndCallBack cameraEndCallBack;
    public CameraStart(Activity activity,CameraEndCallBack cameraEndCallbacks){
       start(activity,cameraEndCallbacks);
    }
    /***
     * 返回的image File
     * **/
    private void start(Activity context,CameraEndCallBack cameraEndCallbacks){
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivityForResult(intent,  1001);
        this.cameraEndCallBack=cameraEndCallbacks;
        /**EventBus***/
        EventBus.getDefault().register(this);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BaseEvent message) {
        if (message.outFile instanceof File){
            File outFile=(File) message.outFile;
            cameraEndCallBack.cameraEnd(outFile);
        }
        stopEventBus();
    }
    public void stopEventBus(){
        EventBus.getDefault().unregister(this);
    }
}
