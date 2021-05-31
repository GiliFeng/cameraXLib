package com.kin.cameralib.camera;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.Toolbar;

import com.kin.cameralib.R;
import com.kin.cameralib.camera.util.DimensionUtil;

/***
 *Created by wu on 2021/4/29
 **/
public class KCameraLayout extends FrameLayout {

    public static int ORIENTATION_PORTRAIT = 0;
    public static int ORIENTATION_HORIZONTAL = 1;

    private int orientation = ORIENTATION_PORTRAIT;
    private View contentView;
    private View centerView;
    private View leftDownView;
    private View leftUpView;
    private View rightUpView;
    private View toolBar;

    private int contentViewId;
    private int centerViewId;
    private int leftDownViewId;
    private int leftUpViewId;
    private int rightUpViewId;
    private int toolBarId;

    public void setOrientation(int orientation) {
        if (this.orientation == orientation) {
            return;
        }
        this.orientation = orientation;
        requestLayout();
    }

    public KCameraLayout(Context context) {
        super(context);
    }

    public KCameraLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(attrs);
    }

    public KCameraLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(attrs);
    }

    {
        setWillNotDraw(false);
    }

    private void parseAttrs(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.OCRCameraLayout,
                0, 0);
        try {
            contentViewId = a.getResourceId(R.styleable.OCRCameraLayout_contentView, -1);
            centerViewId = a.getResourceId(R.styleable.OCRCameraLayout_centerView, -1);
            leftDownViewId = a.getResourceId(R.styleable.OCRCameraLayout_leftDownView, -1);
            leftUpViewId=a.getResourceId(R.styleable.OCRCameraLayout_leftUpView,-1);
            rightUpViewId = a.getResourceId(R.styleable.OCRCameraLayout_rightUpView, -1);
            toolBarId=a.getResourceId(R.styleable.OCRCameraLayout_toolBar,-1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        contentView = findViewById(contentViewId);
        if (centerViewId != -1) {
            centerView = findViewById(centerViewId);/**拍摄**/
        }
        if (leftDownViewId != -1) {
            leftDownView = findViewById(leftDownViewId);/**选择系统相册**/
        }
        if (rightUpViewId != -1) {
            rightUpView = findViewById(rightUpViewId);/**闪光灯**/
        }
        if (leftUpViewId !=-1){/***点击退出activity**/
            leftUpView =findViewById(leftUpViewId);
        }
        if (toolBarId !=-1){
            toolBar=findViewById(toolBarId);
        }
    }

    private Rect backgroundRect = new Rect();
    private Paint paint = new Paint();

    {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(83, 0, 0, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int height = getHeight();
        int left;
        int top;
        if (r < b) {/***竖屏***/
            int contentHeight =height- DimensionUtil.dpToPx(130);// width * 4/3
            int heightTop = height - contentHeight;
            paint.setColor(Color.argb(100, 255, 255, 255));

            if (toolBar !=null){
                toolBar.layout(l,t,r,DimensionUtil.dpToPx(50));
                contentView.layout(l+1, t+DimensionUtil.dpToPx(50), r-2, contentHeight);
            }else{
                contentView.layout(l, t, r, contentHeight);
            }

            backgroundRect.left = 0;
            backgroundRect.top = contentHeight;
            backgroundRect.right = width;
            backgroundRect.bottom = height;

            // layout centerView;
            if (centerView != null) {
                left = (width - centerView.getMeasuredWidth()) / 2;
                top = contentHeight + (heightTop - centerView.getMeasuredHeight()) / 2;
                centerView
                        .layout(left, top, left + centerView.getMeasuredWidth(), top + centerView.getMeasuredHeight());
            }
            // layout leftDownView
            if (leftDownView!=null){
                MarginLayoutParams leftDownViewLayoutParams = (MarginLayoutParams) leftDownView.getLayoutParams();
                left = leftDownViewLayoutParams.leftMargin;
                top = contentHeight + (heightTop - leftDownView.getMeasuredHeight()) / 2;
                leftDownView
                        .layout(left, top, left + leftDownView.getMeasuredWidth(), top + leftDownView.getMeasuredHeight());
            }
            if (rightUpView!=null){
                MarginLayoutParams rightUpViewLayoutParams = (MarginLayoutParams) rightUpView.getLayoutParams();
                left = width - rightUpView.getMeasuredWidth() - rightUpViewLayoutParams.rightMargin;
                top = contentHeight + (heightTop - rightUpView.getMeasuredHeight()) / 2;
                rightUpView.layout(left, top, left + rightUpView.getMeasuredWidth(), top + rightUpView.getMeasuredHeight());
            }
            if(leftUpView!=null){
                MarginLayoutParams leftUpViewLayoutParams = (MarginLayoutParams) leftUpView.getLayoutParams();
                left = leftUpViewLayoutParams.leftMargin;
                top = contentHeight + (heightTop - leftUpView.getMeasuredHeight()) / 2;
                leftUpView.layout(left, top, left + leftUpView.getMeasuredWidth(), top + leftUpView.getMeasuredHeight());
            }
        } else {
            int contentWidth =width- DimensionUtil.dpToPx(130);//height * 4/3
            int widthLeft = width - contentWidth;
            contentView.layout(l, t, contentWidth, height);

            backgroundRect.left = contentWidth;
            backgroundRect.top = 0;
            backgroundRect.right = width;
            backgroundRect.bottom = height;

            // layout centerView
            if (centerView != null) {
                left = contentWidth + (widthLeft - centerView.getMeasuredWidth()) / 2;
                top = (height - centerView.getMeasuredHeight()) / 2;
                centerView
                        .layout(left, top, left + centerView.getMeasuredWidth(), top + centerView.getMeasuredHeight());
            }
            // layout leftDownView
            if (leftDownView!=null) {
                MarginLayoutParams leftDownViewLayoutParams = (MarginLayoutParams) leftDownView.getLayoutParams();
                left = contentWidth + (widthLeft - leftDownView.getMeasuredWidth()) / 2;
                top = height - leftDownView.getMeasuredHeight() - leftDownViewLayoutParams.bottomMargin;
                leftDownView
                        .layout(left, top, left + leftDownView.getMeasuredWidth(), top + leftDownView.getMeasuredHeight());
            }
            // layout rightUpView
            if (rightUpView!=null) {
                MarginLayoutParams rightUpViewLayoutParams = (MarginLayoutParams) rightUpView.getLayoutParams();
                left = contentWidth + (widthLeft - rightUpView.getMeasuredWidth()) / 2;
                top = rightUpViewLayoutParams.topMargin;
                rightUpView.layout(left, top, left + rightUpView.getMeasuredWidth(), top + rightUpView.getMeasuredHeight());
            }
            if(leftUpView!=null){
                MarginLayoutParams leftUpViewLayoutParams = (MarginLayoutParams) leftUpView.getLayoutParams();
                left = leftUpViewLayoutParams.topMargin;
                top = leftUpViewLayoutParams.topMargin;
                leftUpView.layout(left, top, left + leftUpView.getMeasuredWidth(), top + leftUpView.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(backgroundRect, paint);
    }
}
