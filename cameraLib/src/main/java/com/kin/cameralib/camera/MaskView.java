package com.kin.cameralib.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.kin.cameralib.R;

import java.io.File;

/***
 *Created by wu on 2021/4/29
 **/
public class MaskView extends View {

    public static final int MASK_TYPE_NONE = 0;
    public static final int MASK_TYPE_ID_CARD_FRONT = 1;
    public static final int MASK_TYPE_ID_CARD_BACK = 2;
    public static final int MASK_TYPE_BANK_CARD = 11;

    @IntDef({MASK_TYPE_NONE, MASK_TYPE_ID_CARD_FRONT, MASK_TYPE_ID_CARD_BACK, MASK_TYPE_BANK_CARD})
    @interface MaskType {

    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public void setMaskColor(int maskColor) {
        this.maskColor = maskColor;
    }

    private int lineColor = Color.WHITE;

    private int maskType = MASK_TYPE_ID_CARD_FRONT;

    private int maskColor = Color.argb(100, 0, 0, 0);

    private Paint eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint pen = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Rect frame = new Rect();

    private Drawable locatorDrawable;

    public Rect getFrameRect() {
        if (maskType == MASK_TYPE_NONE) {
            return new Rect(0, 0, getWidth(), getHeight());
        } else {
            return new Rect(frame);
        }

    }

    public void setMaskType(@MaskType int maskType) {
        this.maskType = maskType;
        switch (maskType) {
            case MASK_TYPE_ID_CARD_FRONT:
                locatorDrawable = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.bd_ocr_id_card_locator_front, null);
                break;
            case MASK_TYPE_ID_CARD_BACK:
                locatorDrawable = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.bd_ocr_id_card_locator_front, null);
                break;
            case MASK_TYPE_BANK_CARD:
                break;
            case MASK_TYPE_NONE:
            default:
                break;
        }
        invalidate();
    }

    public int getMaskType() {
        return maskType;
    }

    public void setOrientation(@CameraView.Orientation int orientation) {
    }

    public MaskView(Context context) {
        super(context);
        init();
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        locatorDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.bd_ocr_id_card_locator_front, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            if (h<w){/***??????**/
                float ratio = 0.72f;
                int height = (int) (h * ratio);
                int width = height * 620 / 400;
                int left = (w - width) / 2;
                int top = (h - height) / 2;
                int right = width + left;
                int bottom = height + top;
                frame.left = left;
                frame.top = top;
                frame.right = right;
                frame.bottom = bottom;
            }else{/**??????**/
                float ratio =0.8f;
                int width =(int)(w * ratio);
                int height = width;
                int left = (w - width) / 2;
                int top = (h - height) / 2;
                int right = width + left;
                int bottom = height + top;
                frame.left = left;
                frame.top = top;
                frame.right = right;
                frame.bottom = bottom;
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = frame.width();
        int height = frame.height();

        int left = frame.left;
        int top = frame.top;
        int right = frame.right;
        int bottom = frame.bottom;

        canvas.drawColor(maskColor);
        fillRectRound(left, top, right, bottom, 30, 30, false);
        canvas.drawPath(path, pen);/***????????????**/
        canvas.drawPath(path, eraser);/**?????????????????????***/
/**?????????????????????**/
        if (maskType == MASK_TYPE_ID_CARD_FRONT) {
            locatorDrawable.setBounds(
                    (int) (left + 601f / 1006 * width),
                    (int) (top + (110f / 632) * height),
                    (int) (left + (963f / 1006) * width),
                    (int) (top + (476f / 632) * height));
        } else if (maskType == MASK_TYPE_ID_CARD_BACK) {
            locatorDrawable.setBounds(
                    (int) (left + 51f / 1006 * width),
                    (int) (top + (48f / 632) * height),
                    (int) (left + (250f / 1006) * width),
                    (int) (top + (262f / 632) * height));
        }
        if (locatorDrawable != null) {
            locatorDrawable.draw(canvas);
        }
    }

    private Path path = new Path();

    private Path fillRectRound(float left, float top, float right, float bottom, float rx, float ry, boolean
            conformToOriginalPost) {

        path.reset();
        if (rx < 0) {
            rx = 0;
        }
        if (ry < 0) {
            ry = 0;
        }
        float width = right - left;
        float height = bottom - top;
        if (rx > width / 2) {
            rx = width / 2;
        }
        if (ry > height / 2) {
            ry = height / 2;
        }
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.rQuadTo(0, -ry, -rx, -ry);
        path.rLineTo(-widthMinusCorners, 0);
        path.rQuadTo(-rx, 0, -rx, ry);
        path.rLineTo(0, heightMinusCorners);

        if (conformToOriginalPost) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        } else {
            path.rQuadTo(0, ry, rx, ry);
            path.rLineTo(widthMinusCorners, 0);
            path.rQuadTo(rx, 0, rx, -ry);
        }

        path.rLineTo(0, -heightMinusCorners);
        path.close();
        return path;
    }

    {
        // ???????????????????????????????????????
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        pen.setColor(Color.WHITE);
        pen.setStyle(Paint.Style.STROKE);
        pen.setStrokeWidth(6);

        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    private void capture(File file) {

    }
}
