package com.example.sma21_lab5;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //
        Bundle extras = intent.getExtras();
        String chargingStatus = "";

        if (extras != null) {
            String state = extras.getString(BatteryManager.EXTRA_STATUS);
            Log.d("MY_DEBUG_TAG", state);

            if (state.equals(BatteryManager.EXTRA_PLUGGED)) {
                chargingStatus = extras.getString(BatteryManager.EXTRA_PLUGGED);
                Log.d("MY_DEBUG_TAG", chargingStatus);
            }

            if (state.equals(BatteryManager.BATTERY_STATUS_FULL)) {
                chargingStatus = extras.getString(BatteryManager.EXTRA_PLUGGED);
                Log.d("MY_DEBUG_TAG", chargingStatus);
            }
        }

        //
        Intent newIntent = new Intent(context, MainActivity.class);

        newIntent.putExtra("status", chargingStatus);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Charging status changed!")
                .setContentText(chargingStatus)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(MainActivity.notificationId, mBuilder.build());
    }
}
