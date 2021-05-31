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
    public static final int MASK_TYPE_ID_CARD_PERSON = 3;

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

    public void setMaskType(int maskType) {
        this.maskType = maskType;
        switch (maskType) {
            case MASK_TYPE_ID_CARD_FRONT:
                locatorDrawable = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.bd_ocr_id_card_locator_front, null);
                break;
            case MASK_TYPE_ID_CARD_BACK:
                locatorDrawable = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.bd_ocr_id_card_locator_back, null);
                break;
            case MASK_TYPE_ID_CARD_PERSON:
                locatorDrawable = ResourcesCompat.getDrawable(getResources(),
                        R.drawable.bd_ocr_person_locator, null);
                break;
            case MASK_TYPE_NONE:
            default:
                break;
        }
        invalidate();
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
            if (h<w){/***横屏**/
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
            }else{/**竖屏**/
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
        if (width>height){/**横屏***/
            fillRectRound(left, top, right, bottom, 30, 30, false);
            canvas.drawPath(path, pen);/***白色边框**/
            canvas.drawPath(path, eraser);/**中间的白色区域***/
        }else{
            /***绘制圆区域***/
            path.reset();
            path.addCircle(left+width/2,top+height/2,width/2, Path.Direction.CW);
            canvas.drawPath(path, pen);
            canvas.drawPath(path,eraser);
        }
/**身份证人影对应**/
        if (maskType == MASK_TYPE_ID_CARD_FRONT) {
//            locatorDrawable.setBounds((int) (left + 601f / 1006 * width), (int) (top + (110f / 632) * height), (int) (left + (963f / 1006) * width), (int) (top + (476f / 632) * height));
            locatorDrawable.setBounds(
                    (int) (left + 0 / 1006 * width),
                    (int) (top + (0 / 632) * height),
                    (int) (left + (1006 / 1006) * width),
                    (int) (top + (632 / 632) * height));
        } else if (maskType == MASK_TYPE_ID_CARD_BACK) {
            locatorDrawable.setBounds(
                    (int) (left + 0 / 1006 * width),
                    (int) (top + (0 / 632) * height),
                    (int) (left + (1006 / 1006) * width),
                    (int) (top + (632 / 632) * height));
        }else if (maskType ==MASK_TYPE_ID_CARD_PERSON){
            locatorDrawable.setBounds(
                    (int) (left + 50f / 1006 * width),
                    (int) (top + (50f / 1006) * height),
                    (int) (left + (956f / 1006) * width),
                    (int) (top + (956f / 1006) * height));
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
        // 硬件加速不支持，图层混合。
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        pen.setColor(getResources().getColor(R.color.yellow));
        pen.setStyle(Paint.Style.STROKE);
        pen.setStrokeWidth(6);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    private void capture(File file) {

    }
}
