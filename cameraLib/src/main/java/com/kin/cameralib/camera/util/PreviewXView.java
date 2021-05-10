package com.kin.cameralib.camera.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;

/***
 *Created by wu on 2021/5/8
 **/
public class PreviewXView extends PreviewView {
    public PreviewXView(@NonNull Context context) {
        super(context);
    }

    public PreviewXView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewXView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
