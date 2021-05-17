package com.kin.cameralib.camera.event;

import java.io.File;

/***
 *Created by wu on 2021/5/17
 **/
public class BaseEvent {

    public final Object outFile;

    public static BaseEvent getInstance(Object outFile) {
        return new BaseEvent(outFile);
    }

    private BaseEvent(Object outFile) {
        this.outFile = outFile;
    }
}