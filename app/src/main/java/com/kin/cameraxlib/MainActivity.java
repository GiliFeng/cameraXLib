package com.kin.cameraxlib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kin.cameralib.CameraActivity;
import com.kin.cameralib.CameraStart;
import com.kin.cameralib.camera.util.CameraEndCallBack;

import java.io.File;

import static com.kin.cameralib.CameraActivity.KEY_OUTPUT_FILE_PATH;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvFront=findViewById(R.id.tv_front);
        TextView tv_back=findViewById(R.id.tv_back);
        TextView tv_person=findViewById(R.id.tv_person);
        tv_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraStart.start(MainActivity.this, CameraStart.CONTENT_CAMERA_TYPE.CONTENT_TYPE_PERSON, new CameraEndCallBack() {
                    @Override
                    public void cameraEnd(int resultCode, File outFile) {
                        ImageView imageView = findViewById(R.id.photo);
                        imageView.setImageURI(Uri.fromFile(outFile));
                    }
                });

            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraStart.start(MainActivity.this, CameraStart.CONTENT_CAMERA_TYPE.CONTENT_TYPE_ID_CARD_BACK, new CameraEndCallBack() {
                    @Override
                    public void cameraEnd(int resultCode, File outFile) {
                        ImageView imageView = findViewById(R.id.photo);
                        imageView.setImageURI(Uri.fromFile(outFile));
                    }
                });
            }
        });
        tvFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraStart.start(MainActivity.this, CameraStart.CONTENT_CAMERA_TYPE.CONTENT_TYPE_ID_CARD_FRONT, new CameraEndCallBack() {
                    @Override
                    public void cameraEnd(int resultCode, File outFile) {
                        ImageView imageView = findViewById(R.id.photo);
                        imageView.setImageURI(Uri.fromFile(outFile));
                    }
                });
            }
        });
    }
}