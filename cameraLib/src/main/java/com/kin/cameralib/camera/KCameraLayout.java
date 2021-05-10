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
    private View rightUpView;

    private int contentViewId;
    private int centerViewId;
    private int leftDownViewId;
    private int rightUpViewId;

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
            rightUpViewId = a.getResourceId(R.styleable.OCRCameraLayout_rightUpView, -1);
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
        if (r < b) {
            int contentHeight =width- DimensionUtil.dpToPx(130);// width * 4/3
            int heightLeft = height - contentHeight;
            contentView.layout(l, t, r, contentHeight);

            backgroundRect.left = 0;
            backgroundRect.top = contentHeight;
            backgroundRect.right = width;
            backgroundRect.bottom = height;

            // layout centerView;
            if (centerView != null) {
                left = (width - centerView.getMeasuredWidth()) / 2;
                top = contentHeight + (heightLeft - centerView.getMeasuredHeight()) / 2;
                centerView
                        .layout(left, top, left + centerView.getMeasuredWidth(), top + centerView.getMeasuredHeight());
            }
            // layout leftDownView
            if (leftDownView!=null){
                MarginLayoutParams leftDownViewLayoutParams = (MarginLayoutParams) leftDownView.getLayoutParams();
                left = leftDownViewLayoutParams.leftMargin;
                top = contentHeight + (heightLeft - leftDownView.getMeasuredHeight()) / 2;
                leftDownView
                        .layout(left, top, left + leftDownView.getMeasuredWidth(), top + leftDownView.getMeasuredHeight());
            }
            if (rightUpView!=null){
                MarginLayoutParams rightUpViewLayoutParams = (MarginLayoutParams) rightUpView.getLayoutParams();
                left = width - rightUpView.getMeasuredWidth() - rightUpViewLayoutParams.rightMargin;
                top = contentHeight + (heightLeft - rightUpView.getMeasuredHeight()) / 2;
                rightUpView.layout(left, top, left + rightUpView.getMeasuredWidth(), top + rightUpView.getMeasuredHeight());
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
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(backgroundRect, paint);
    }
}
