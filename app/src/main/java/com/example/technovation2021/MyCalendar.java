package com.example.technovation2021;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MyCalendar extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calendar);

        //HashSet<Date> events = new HashSet<>();
        //events.add(new Date());

        CustomCalendar cv = ((CustomCalendar)findViewById(R.id.custom_calendar));
        cv.updateCalendar(LocalDate.now());//events);

        // assign event handler
        /*
        cv.setEventHandler(new CustomCalendar.EventHandler()
        {
            @Override
            public void onDayLongPress(Date date)
            {
                // show events of this date in another activity.
                DateFormat df = SimpleDateFormat.getDateInstance();
                Toast.makeText(MyCalendar.this, df.format(date), Toast.LENGTH_SHORT).show();
            }
        });

         */
    }
}