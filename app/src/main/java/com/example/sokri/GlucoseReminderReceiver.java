// GlucoseReminderReceiver.java
package com.example.sokri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class GlucoseReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "glucose_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Vérifiez si le canal existe déjà
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(
                        CHANNEL_ID, "Rappel glycémie", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Rappel Glycémie")
                .setContentText("Il est temps de mesurer votre glycémie.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(1, notification);
    }
}

