package com.example.sma21_lab5;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.TextView;

public class PhoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //
        TextView textView = ((MainActivity)context).findViewById(R.id.textView);

        String intentAction = intent.getAction();

        if (intentAction.equals(Intent.ACTION_BATTERY_CHANGED))
        {
            int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            StringBuffer message = new StringBuffer("isFull: ");

            int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            if (batteryStatus == BatteryManager.BATTERY_STATUS_FULL ||
                    batteryLevel == 100)
            {
                message.append("yes");
            }
            else
            {
                message.append("no");
            }

            message.append("\nisCharging: ");
            if (batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING)
            {
                message.append("yes");
            }
            else
            {
                message.append("no");
            }


            // set battery text view according to battery status
            textView.setText(message);
        }
    }
}
