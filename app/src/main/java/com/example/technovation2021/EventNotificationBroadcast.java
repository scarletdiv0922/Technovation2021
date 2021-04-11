package com.example.technovation2021;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

import static com.example.technovation2021.DailyEventsAdapter.LOG_TAG;

public class EventNotificationBroadcast extends BroadcastReceiver {
    ArrayList<Event> evList = new ArrayList<Event>();
    boolean eventsFetched = false;
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

        // Intent notifIntent = new Intent (notifContext, AddTask.class);
        Intent notifIntent = new Intent(notifContext, DailyEvents.class);
        Log.d("Current Date", "currentdate= "+currentDate);
        notifIntent.putExtra("EventsForDay", currentDate);


       // eventHandler.onDayLongPress((LocalDate)view.getItemAtPosition(position));

        //notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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










//Query (find) all events for today
/*        FirebaseAuth mAuth= FirebaseAuth.getInstance();

        try {
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            String userId = mAuth.getCurrentUser().getUid();

            Query eventList = mDatabase.child(userId).child("eventList").orderByChild("date").
                    startAt("2021-04-08").endAt("2021-04-08");

            //Query eventList = mDatabase.child(userId).child("activityList").orderByChild("startDate").equalTo("2021-04-15").endAt()

            eventList.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        evList.add(ds.getValue(Event.class));
                    }
                    eventsFetched = true;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Log.d(LOG_TAG, "Wait for events data fetch");
            while ((eventsFetched == false))
                Thread.yield();
            Log.d(LOG_TAG, " data fetch done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        //Loop through the events for the day and set a notification for 10 minutes before the notification start time
        Log.d(LOG_TAG, "I am here and this is the evList size" + evList.size());
        */ //TODO: App is not responding when we have this code