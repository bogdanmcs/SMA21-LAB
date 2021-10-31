package com.example.sma21_lab5;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private PhoneReceiver phoneReceiver = new PhoneReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onResume () {
        super.onResume();
        registerReceiver(phoneReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }
}