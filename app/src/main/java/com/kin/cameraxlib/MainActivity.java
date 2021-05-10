package com.kin.cameraxlib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kin.cameralib.CameraActivity;

import java.io.File;

import static com.kin.cameralib.CameraActivity.KEY_OUTPUT_FILE_PATH;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvFront=findViewById(R.id.tvFront);
        tvFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent,  1001);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case RESULT_OK:
                ImageView imageView = findViewById(R.id.photo);
                Bundle bundle = data.getExtras();  //data为B中回传的Intent
                File file = (File) bundle.get(KEY_OUTPUT_FILE_PATH);//str即为回传的值
                imageView.setImageURI(Uri.fromFile(file));
                break;
            default:
                break;
        }
    }
}