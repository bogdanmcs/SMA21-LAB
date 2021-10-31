package com.example.sma21_lab5;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "channel_battery";
    private PowerConnectionReceiver powerConnectionReceiver = new PowerConnectionReceiver();

    public void onCreate () {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel () {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Main channel.");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
