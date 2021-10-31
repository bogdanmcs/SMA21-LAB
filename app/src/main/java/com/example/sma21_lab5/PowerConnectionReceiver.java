package com.example.sma21_lab5;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.BatteryManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class PowerConnectionReceiver extends BroadcastReceiver {
    private int batteryStatus = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, intent);
        asyncTask.execute();

        String intentAction = intent.getAction();
        if (intentAction.equals(Intent.ACTION_BATTERY_CHANGED))
        {
            String message;
            boolean continue_f = false;

            if (batteryStatus == -1 ||
                    (batteryStatus != intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)))
            {
                batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                continue_f = true;
            }

            if (continue_f)
            {
                if (batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
                    message = "Phone is charging";
                } else {
                    message = "Phone is not charging";
                }

                // notifications
                Intent newIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_IMMUTABLE);

                Notification notification = new NotificationCompat.Builder(context, App.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Charging status changed!")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pendingIntent)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(1, notification);
            }
        }
    }

    private static class Task extends AsyncTask<String, String, String> {

        private final PendingResult pendingResult;
        private final Intent intent;

        private Task(PendingResult pendingResult, Intent intent) {
            super();
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        @Override
        protected String doInBackground(String... strings) {
            return "BATT";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish();
        }
    }
}
