package com.example.sma21_lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "1";
    public static int notificationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // write code here.
        TextView textView = findViewById(R.id.textView);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, intentFilter);

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = (
                    status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL
                );

        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = (
                    chargePlug == BatteryManager.BATTERY_PLUGGED_USB
                );
        boolean acCharge = (
                    chargePlug == BatteryManager.BATTERY_PLUGGED_AC
                );

        StringBuffer textViewBuilder = new StringBuffer("Battery status:")
                .append("\nextraStatusInt = " + String.valueOf(status))
                .append("\nisCharging = " + String.valueOf(isCharging))
                .append("\nusbCharge = " + String.valueOf(usbCharge))
                .append("\nacCharge = " + String.valueOf(acCharge));

        textView.setText(textViewBuilder);
    }
}