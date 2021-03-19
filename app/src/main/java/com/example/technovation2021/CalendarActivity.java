//Month Calendar
package com.example.technovation2021;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Date;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class CalendarActivity extends AppCompatActivity {

    private EventList evList;
    private SQLiteDatabase sqlDb;
    private static final String dbName = "PASchedulerDB";
    private static final String LOG_TAG = CalendarActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        Log.d("CalendarXX", "call custom calendar");
        CustomCalendar cv = findViewById(R.id.custom_calendar);
        cv.updateCalendar(events);
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
            case R.id.addEvent:
                Intent intent2 = new Intent( CalendarActivity.this, AddActivity.class);
                startActivity(intent2);
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}
