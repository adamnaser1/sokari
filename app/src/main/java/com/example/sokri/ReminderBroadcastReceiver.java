package com.example.sokri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "glucose_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ReminderBroadcastReceiver", "onReceive called");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Rappel trimestriel de glycémie", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Build and show the notification
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("w9ayet controle ")
                .setContentText("w9ayet ta3ml ta7lil ma5zoun w temchi l tbibek")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }

}
