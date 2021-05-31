package com.kin.cameralib.camera.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kin.cameralib.R;
import com.kin.cameralib.camera.KCameraLayout;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/***
 *Created by wu on 2021/5/28
 **/
public class PopWindowUtil {
    public static PopupWindow showInfo(Activity context) {
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
                R.layout.popwindow_show_info, null, true);
        PopupWindow pw = new PopupWindow(menuView, DimensionUtil.dpToPx(320),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pw.setOutsideTouchable(false); // 判断点击当前的popupWindow区域外的touch事件是否有效
        pw.setFocusable(false);//判断当前的popupWindow是否获取焦点
        pw.setBackgroundDrawable(context.getDrawable(R.drawable.bd_popwindow_circle));

        backgroundAlpha(0.5f,context);
//        pw.setAnimationStyle(R.style.nornal_style); // 设置动画
        KCameraLayout takePictureContainer = (KCameraLayout) context.findViewById(R.id.take_picture_container);
//方法二
        pw.showAtLocation(takePictureContainer, Gravity.CENTER,0,0);
//添加pop窗口关闭事件监听
//        pw.setOnDismissListener(new poponDismissListener());
        menuView.findViewById(R.id.btn_popwindw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backgroundAlpha(1.0f,context);
                pw.dismiss();
            }
        });
        return pw;
    }

    public static void backgroundAlpha(float bgAlpha,Activity context) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        context.getWindow().setAttributes(lp);
    }
}
