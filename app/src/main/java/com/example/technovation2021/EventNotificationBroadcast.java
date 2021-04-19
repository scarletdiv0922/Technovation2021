package com.example.technovation2021;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.time.LocalDate;
import java.util.ArrayList;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventNotificationBroadcast extends BroadcastReceiver {

    String currentDate= LocalDate.now().toString();

    public void onReceive(Context notifContext, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            int priority = NotificationCompat.PRIORITY_MAX;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = notifContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notifIntent = new Intent(notifContext, DailyEvents.class);
        notifIntent.putExtra("EventsForDay", currentDate);

        PendingIntent pendingIntent = PendingIntent.getActivity(notifContext, 0, notifIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(notifContext, "1");
        mBuilder.setContentTitle("TaskMate: " + intent.getStringExtra("EventDesc") + " is coming up");

String[] quotes = new String[] {"\"It takes both a plan and a schedule to get things done.\"",
        "\"A schedule defends from chaos and whim. A net for catching days.\"",
        "\"Good things take time.\"",
        "\"You can totally do this.\"",
        "\"Dreams don't work unless you do.\"",
        "\"A mistake is success in progress.\"",
        "\"Passion + Consistency = Success\"",
        "\"When nothing goes right, go left.\"",
        "\"Try Again. Fail again. Fail better.\"",
        "\"Change the world by being yourself.\"",
        "\"Every moment is a fresh beginning.\"",
        "\"Yesterday you said tomorrow. Just do it.\"",
        "\"There is no substitute for hard work.\"",
        "\"Make each day your masterpiece.\"",
        "\"Dream big and dare to fail.\"",
        "\"Wherever you go, go with all your heart.\"",
        "\"Once you choose hope, anything’s possible.\"",
        "\"Life's a journey, not a race.\"",};
        String randomQuote = quotes[(int) (Math.random() * quotes.length)];
        mBuilder.setContentText(randomQuote);

        mBuilder.setSmallIcon(R.drawable.tm5);
        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat myNotificationManager= NotificationManagerCompat.from(notifContext);
        myNotificationManager.notify(1, mBuilder.build());
    }
}
