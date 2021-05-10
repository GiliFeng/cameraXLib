package com.kin.cameralib.camera.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.WindowManager;

/***
 *Created by wu on 2021/4/29
 **/
public class DimensionUtil {

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    public static int getScreenWidth(Context context) {
        WindowManager manager=(WindowManager) (context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        int screenWidth=  manager.getDefaultDisplay().getWidth();
        return screenWidth;
    }
    public static int getScreenHeight(Context context) {
        WindowManager manager=(WindowManager) (context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        int screenHeight=  manager.getDefaultDisplay().getHeight();
        return screenHeight;
    }
    //获取系统状态栏高度
    public static int getStatusHeight(Context context) {
        //单位px
        int statusHeight = -1;
        try {
            Class clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object)
                    .toString());
            statusHeight = context.getResources().getDimensionPixelOffset(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
