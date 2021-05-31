package com.kin.cameralib.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.camera.core.CameraSelector;

import com.kin.cameralib.R;
import com.kin.cameralib.camera.util.BitmapUtil;
import com.kin.cameralib.camera.util.DimensionUtil;
import com.kin.cameralib.camera.util.ICameraControl;
import com.kin.cameralib.camera.util.ImageUtil;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;
import static com.kin.cameralib.camera.MaskView.MASK_TYPE_ID_CARD_PERSON;

/***
 *Created by wu on 2021/4/29
 **/
public class CameraView extends FrameLayout {

    /**
     * 照相回调
     */
    public interface OnTakePictureCallback {
        void onPictureTaken(Bitmap bitmap);
    }

    /**
     * 垂直方向 {@link #setOrientation(int)}
     */
    public static final int ORIENTATION_PORTRAIT = 0;
    /**
     * 水平方向 {@link #setOrientation(int)}
     */
    public static final int ORIENTATION_HORIZONTAL = 90;
    /**
     * 水平翻转方向 {@link #setOrientation(int)}
     */
    public static final int ORIENTATION_INVERT = 270;

    @IntDef({ORIENTATION_PORTRAIT, ORIENTATION_HORIZONTAL, ORIENTATION_INVERT})
    public @interface Orientation {

    }

    private CameraViewTakePictureCallback cameraViewTakePictureCallback = new CameraViewTakePictureCallback();

//    private ICameraControl cameraControl;
    private CameraXControl cameraControl;
    /**
     * 相机预览View
     */
    private View displayView;
    /**
     * 身份证，银行卡，等裁剪用的遮罩
     */
    private MaskView maskView;

    /**
     * 用于显示提示证 "请对齐身份证正面" 之类的
     */
    private TextView hintView;
    private int maskType;/**CameraView 类型***/

    public ICameraControl getCameraControl() {
        return cameraControl;
    }

    public void setOrientation(@Orientation int orientation) {
        cameraControl.setDisplayOrientation(orientation);
    }
    private Context context;

    public CameraView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    public void start() {
        cameraControl.start();
        setKeepScreenOn(true);
    }

    public void stop() {
        cameraControl.stop();
        setKeepScreenOn(false);
    }

    public void takePicture(final File file, final OnTakePictureCallback callback) {
        cameraViewTakePictureCallback.file = file;
        cameraViewTakePictureCallback.callback = callback;
        cameraControl.takePicture(cameraViewTakePictureCallback);
    }

    public void setMaskType(int maskType) {
        this.maskType=maskType;
        maskView.setMaskType(maskType);

        maskView.setVisibility(VISIBLE);
        hintView.setVisibility(VISIBLE);

        String hintStr =context.getString(R.string.camera_hint_front);
        switch (maskType) {
            case MaskView.MASK_TYPE_ID_CARD_FRONT:
                hintStr = context.getString(R.string.camera_hint_front);
                break;
            case MaskView.MASK_TYPE_ID_CARD_BACK:
                hintStr = context.getString(R.string.camera_hint_back);
                break;
            case MASK_TYPE_ID_CARD_PERSON:
                hintStr = context.getString(R.string.camera_hint_person);
                break;
            case MaskView.MASK_TYPE_NONE:
            default:
                maskView.setVisibility(INVISIBLE);
                hintView.setVisibility(INVISIBLE);
                break;
        }
        hintView.setText(hintStr);
    }

    private void init() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            cameraControl = new CameraCurControl(getContext());
//        } else {
//            cameraControl = new CameraOriControl(getContext());
//        }
        if (DimensionUtil.getScreenWidth(context)>DimensionUtil.getScreenHeight(context)){/**横屏***/
            cameraControl =new CameraXControl(getContext(), CameraSelector.DEFAULT_BACK_CAMERA);
        }else{
            cameraControl =new CameraXControl(getContext(), CameraSelector.DEFAULT_FRONT_CAMERA);
        }
        displayView = cameraControl.getDisplayView();
        addView(displayView);
        maskView = new MaskView(getContext());
        addView(maskView);
        hintView = new TextView(getContext());
        hintView.setTextColor(Color.WHITE);
        hintView.setTextSize(DimensionUtil.spToPx(4));
        addView(hintView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        displayView.layout(left, 0, right, bottom - top);
        maskView.layout(left, 0, right, bottom - top);
        /****拍照底下的介绍文字***/
        if (DimensionUtil.getScreenWidth(context)>DimensionUtil.getScreenHeight(context)){/***横屏**/
            int hintViewWidth = DimensionUtil.dpToPx(getTxtWidth());
            int hintViewHeight = DimensionUtil.dpToPx(40);
            int hintViewLeft = (getWidth() - hintViewWidth) / 2;
            int hintViewTop = maskView.getFrameRect().bottom + DimensionUtil.dpToPx(8);
            hintView.layout(hintViewLeft, hintViewTop, hintViewLeft + hintViewWidth, hintViewTop + hintViewHeight);
        }else{
            int hintViewHeight = DimensionUtil.dpToPx(40);
            int hintViewTop = maskView.getFrameRect().bottom + DimensionUtil.dpToPx(8);
            hintView.layout(DimensionUtil.dpToPx(20), hintViewTop, getWidth()-DimensionUtil.dpToPx(20), hintViewTop + hintViewHeight);
        }
    }
    private int getTxtWidth(){/***获取text长度**/
        Paint paint = new Paint();
        paint.setTextSize(DimensionUtil.spToPx(4));
        String text=hintView.getText().toString();
        float size = paint.measureText(text);
        return (int)size;
    }

    /**
     * 拍摄后的照片。需要进行裁剪。有些手机（比如三星）不会对照片数据进行旋转，而是将旋转角度写入EXIF信息当中，
     * 所以需要做旋转处理。
     *
     * @param outputFile 写入照片的文件。
     * @param imageFile  原始照片。
     * @param rotation   照片exif中的旋转角度。
     *
     * @return 裁剪好的bitmap。
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Bitmap crop(File outputFile, File imageFile, int rotation) {
        try {
            Bitmap orimap=BitmapUtil.decodeFile(imageFile.getAbsolutePath());/***原图片**/
            // BitmapRegionDecoder不会将整个图片加载到内存。
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(imageFile.getAbsolutePath(), false);
            Rect previewFrame = cameraControl.getPreviewFrame();
            int width = rotation % 180 == 0 ? decoder.getWidth() : decoder.getHeight();
            int height = rotation % 180 == 0 ? decoder.getHeight() : decoder.getWidth();

            Rect frameRect = maskView.getFrameRect();
            int left =frameRect.left;
            int top = frameRect.top;
            int right = frameRect.right;
            int bottom = frameRect.bottom;
            if (DimensionUtil.getScreenWidth(context)>DimensionUtil.getScreenHeight(context)) {/**横屏***/
                left=frameRect.left+(width-previewFrame.width())/2;
                right=frameRect.right+(width-previewFrame.width())/2;
            }else{
                top=frameRect.top+(height-previewFrame.height())/2;
                bottom=frameRect.bottom+(height-previewFrame.height())/2;
            }
            // 高度大于图片
            if (previewFrame.top < 0) {
                // 宽度对齐。
                int adjustedPreviewHeight = previewFrame.height() * getWidth() / previewFrame.width();
                int topInFrame = ((adjustedPreviewHeight - frameRect.height()) / 2)
                        * getWidth() / previewFrame.width();
                int bottomInFrame = ((adjustedPreviewHeight + frameRect.height()) / 2) * getWidth()
                        / previewFrame.width();

                // 等比例投射到照片当中。
                top = topInFrame * height / previewFrame.height();
                bottom = bottomInFrame * height / previewFrame.height();
            } else {
                // 宽度大于图片
                if (previewFrame.left < 0) {
                    // 高度对齐
                    int adjustedPreviewWidth = previewFrame.width() * getHeight() / previewFrame.height();
                    int leftInFrame = ((adjustedPreviewWidth - maskView.getFrameRect().width()) / 2) * getHeight()
                            / previewFrame.height();
                    int rightInFrame = ((adjustedPreviewWidth + maskView.getFrameRect().width()) / 2) * getHeight()
                            / previewFrame.height();

                    // 等比例投射到照片当中。
                    left = leftInFrame * width / previewFrame.width();
                    right = rightInFrame * width / previewFrame.width();
                }
            }
            Rect region = new Rect();
                region.left = left;
                region.top = top;
                region.right = right;
                region.bottom = bottom;
            // 90度或者270度旋转
            if (rotation % 180 == 90) {
                int x = decoder.getWidth() / 2;
                int y = decoder.getHeight() / 2;

                int rotatedWidth = region.height();
                int rotated = region.width();

                // 计算，裁剪框旋转后的坐标
                region.left = x - rotatedWidth / 2;
                region.top = y - rotated / 2;
                region.right = x + rotatedWidth / 2;
                region.bottom = y + rotated / 2;
                region.sort();
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            //            options.inPreferredConfig = Bitmap.Config.RGB_565;

            // 最大图片大小。
            int maxPreviewImageSize = 2560;
            int size = Math.min(decoder.getWidth(), decoder.getHeight());
            size = Math.min(size, maxPreviewImageSize);

            options.inSampleSize = ImageUtil.calculateInSampleSize(options, size, size);
            options.inScaled = true;
            options.inDensity = Math.max(options.outWidth, options.outHeight);
            options.inTargetDensity = size;

            Bitmap bitmap = decoder.decodeRegion(region, options);
            if (this.maskType==MASK_TYPE_ID_CARD_PERSON) {/**自拍,前置摄像头左右翻转***/
               bitmap=BitmapUtil.convertBmp(bitmap);
            }
            if (rotation != 0) {
                // 只能是裁剪完之后再旋转了。有没有别的更好的方案呢？
                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                Bitmap rotatedBitmap = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                if (bitmap != rotatedBitmap) {
                    // 有时候 createBitmap会复用对象
                    bitmap.recycle();
                }
                bitmap = rotatedBitmap;
            }

            try {
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (cameraViewTakePictureCallback.thread != null) {
            cameraViewTakePictureCallback.thread.quit();
        }
    }

    private class CameraViewTakePictureCallback implements ICameraControl.OnTakePictureCallback {

        private File file;
        private OnTakePictureCallback callback;

        HandlerThread thread = new HandlerThread("cropThread");
        Handler handler;

        {
            thread.start();
            handler = new Handler(thread.getLooper());
        }

        @Override
        public void onPictureTaken(final byte[] data) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        final int rotation = ImageUtil.getOrientation(data);
                        final File tempFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), "jpg");
                        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                        fileOutputStream.write(data);
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = crop(file, tempFile, rotation);
                                callback.onPictureTaken(bitmap);
                                boolean deleted = tempFile.delete();
                                if (!deleted) {
                                    tempFile.deleteOnExit();
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}

