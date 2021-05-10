package com.kin.cameralib.camera.util;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.content.ContentValues.TAG;

/***
 *Created by wu on 2021/4/29
 **/
public class PicSaveUtil {
    //保存资源文件中的图片到本地相册,实时刷新
    public static void saveImageToGallery(Context context, Bitmap bmp,File file,String fileName){
        if (Build.VERSION.SDK_INT < 29) {
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "saveImageToGallery: " + file.getAbsolutePath() + "-----" + fileName);
            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);// 设置显示名称
            contentValues.put(MediaStore.Images.Media.DESCRIPTION, fileName); //设置文件说明
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");//设置图片类型
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);//用getContentResolver.insert ()外部存储存放
            try {
                OutputStream fos = context.getContentResolver().openOutputStream(uri);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);//压缩质量
                if (fos != null) {
                    fos.flush();
                    fos.close();
                    fos = null;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


/**

 * 根据URI获取bitmap

 * @param bitmap

 * @param context

 * @return

 * @throws IOException

 */
        public static Bitmap getBitmapFromUri(Context context, Bitmap bitmap) throws IOException{
            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, null,null));
            ParcelFileDescriptor parcelFileDescriptor =context.getContentResolver().openFileDescriptor(uri, "r");//相当于读取文件的流
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        }
}
