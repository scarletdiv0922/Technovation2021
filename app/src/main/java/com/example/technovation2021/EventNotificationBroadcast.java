package com.example.technovation2021;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class EventNotificationBroadcast extends BroadcastReceiver {

    public void onReceive(Context notifContext, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
        //    int priority = NotificationCompat.PRIORITY_MAX;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = notifContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        Intent notifIntent = new Intent (notifContext, AddTask.class);
        notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(notifContext, 0, notifIntent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(notifContext, "1");
        mBuilder.setContentTitle("Event Notification Title");
        mBuilder.setContentText("This is a notification for an event");
        mBuilder.setSmallIcon(R.drawable.next_icon);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat myNotificationManager= NotificationManagerCompat.from(notifContext);
        myNotificationManager.notify(1, mBuilder.build());
    }
}