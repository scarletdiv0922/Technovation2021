package com.example.technovation2021;

import android.os.Bundle;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import androidx.appcompat.app.AppCompatActivity;

public class MyCalendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar);

        HashSet<Date> events = new HashSet<>();
        events.add(new Date());

        CustomCalendar cv = ((CustomCalendar)findViewById(R.id.custom_calendar));
        cv.updateCalendar(events);

        // assign event handler
        cv.setEventHandler(new CustomCalendar.EventHandler()
        {
            @Override
            public void onDayLongPress(Date date)
            {
                // show returned day
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(MyCalendar.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });
    }
}