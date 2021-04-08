//Month Calendar
package com.example.technovation2021;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.widget.Toast;
import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    private static final String LOG_TAG = CalendarActivity.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        CustomCalendar cv = findViewById(R.id.custom_calendar);


        Intent notifIntent = new Intent (CalendarActivity.this, EventNotificationBroadcast.class);
        PendingIntent notifPendingIntent = PendingIntent.getBroadcast(CalendarActivity.this, 0, notifIntent, 0);

        AlarmManager notifAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long currentTime = System.currentTimeMillis();
        long tenSeconds = 1000*10;

        notifAlarmManager.set(AlarmManager.RTC_WAKEUP,
                currentTime+tenSeconds,
                notifPendingIntent);

        //cv.setContext(CalendarActivity.this);
        Log.d("CalendarXX", "call custom calendar");
        /* Below handler works but may not be needed.
        cv.setEventHandler(new CustomCalendar.EventHandler()
        {
            @Override
            public void onDayLongPress(LocalDate date)
            {
                // Expand events of day.
                Log.d(LOG_TAG, "show all events for:" + date.toString());
            }
        });
         */
        cv.updateCalendar(LocalDate.now());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cal_act_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.settingMenu:
                //Toast.makeText(CalendarActivity.this, "Go to settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CalendarActivity.this, UserSettings.class);
                startActivity(intent);
                return true;

            case R.id.studentActivity:
                Intent intent2 = new Intent( CalendarActivity.this, StudentActivity.class);
                startActivity(intent2);
                return true;

            case R.id.addTask:
                Intent intent3 = new Intent( CalendarActivity.this, AddTask.class);
                startActivity(intent3);
                return true;

            case R.id.notifications:
                Intent intent4 = new Intent( CalendarActivity.this, StudentNotification.class);
                startActivity(intent4);
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}

/*
11:59
12:00am
00:00am
00:01am
00:59
01:00
11:59am
12:00pm
12:01pm
12:59
01:00
11:59pm
 */