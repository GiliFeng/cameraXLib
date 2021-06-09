package com.kin.cameralib.camera.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/***
 *Created by wu on 2021/4/29
 **/
public class UIImageView extends ImageView {
    public int width;
    public int height;
    public Paint mPaint;
    public Path mPath;
    public RectF mRectF;
    public UIImageView(Context context) {
        super(context);
    }

    public UIImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UIImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
        init();
    }
    private void init(){
        mPaint = new Paint();
        //画笔加上  抗锯齿标志，图像更加平滑
        mPaint.setAntiAlias(true);
        //如果该项设置为true,则图像在动画进行中会滤掉对Bitmap图像的优化操作,加快显示
        mPaint.setFilterBitmap(true);
        //防抖动
        mPaint.setDither(true);
        //画笔的颜色
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPath=new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width>height){/***横屏***/
            int contentH=(int)(height * 0.72f);
            int contentW = contentH * 620 / 400;
            int padLeft = (width-contentW)/2;
            int padTop=(height-contentH)/2;

            mRectF = new RectF(padLeft, padTop, contentW+padLeft,contentH+padTop);
            Rect rect = null;/**是否裁剪，不裁剪则为null**/
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            if (getWidth() == 0 || getHeight() == 0) {
                return;
            }
            //获取图片，转化为Bitmap
            Bitmap b =  ((BitmapDrawable)drawable).getBitmap();
            if(null == b)
            {
                return;
            }
            //将图片转为32位ARGB位图，保证图片质量
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            //path划出一个圆角矩形，容纳图片,图片矩形区域设置比红色外框小，否则会覆盖住外框，随意控制
            mPath.addRoundRect(mRectF, 30, 30, Path.Direction.CW);
            canvas.drawRoundRect(mRectF, 30, 30, mPaint); //画出红色外框圆角矩形
            canvas.clipPath(mPath);//将canvas裁剪到path设定的区域，往后的绘制都只能在此区域中，
            canvas.drawBitmap(bitmap,rect,mRectF,mPaint);
        }else{
            int contentW=(int)(width * 0.8f);
            int contentH=contentW;
            int padLeft = (width-contentW)/2;
            int padTop=(height-contentH)/2;

            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            if (getWidth() == 0 || getHeight() == 0) {
                return;
            }
            //获取图片，转化为Bitmap
            Bitmap b =  ((BitmapDrawable)drawable).getBitmap();
            if(null == b)
            {
                return;
            }
            //将图片转为32位ARGB位图，保证图片质量
            Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);
            mRectF = new RectF(padLeft, padTop, contentW+padLeft,contentH+padTop);
            //path划出一个圆角矩形，容纳图片,图片矩形区域设置比红色外框小，否则会覆盖住外框，随意控制
            mPath.addRoundRect(mRectF, width/2, width/2, Path.Direction.CW);
            canvas.drawRoundRect(mRectF, width/2, width/2, mPaint); //画出白色外框圆角矩形
            canvas.clipPath(mPath);//将canvas裁剪到path设定的区域，往后的绘制都只能在此区域中，
            canvas.drawBitmap(bitmap,null,mRectF,mPaint);
        }
    }
}
