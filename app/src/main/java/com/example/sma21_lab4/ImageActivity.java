package com.example.sma21_lab4;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        MyApplication myApplication = (MyApplication) getApplicationContext();

        if(myApplication.getBitmap() == null)
        {
            Toast.makeText(this,"Error transmitting URL",Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(myApplication.getBitmap());
        }
    }
}
