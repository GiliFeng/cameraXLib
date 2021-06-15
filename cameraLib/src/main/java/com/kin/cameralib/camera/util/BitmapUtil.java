package com.kin.cameralib.camera.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/***
 *Created by wu on 2021/6/15
 **/
public class BitmapUtil {
    /***byte[]转化为bitmap**/
    public static Bitmap decodeFrameToBitmap(byte[] frame, int width, int height)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(frame, 0, frame.length);
        return bitmap;
    }
}
