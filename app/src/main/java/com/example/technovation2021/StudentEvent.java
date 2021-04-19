package com.example.technovation2021;

import android.os.Build;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import androidx.annotation.RequiresApi;

public class StudentEvent implements Serializable {

    public String eventName;
    public LocalDateTime startDateTime; //Start date with start time
    public LocalDateTime endDateTime; //End date with end time


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getStartDateTime() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        return startDateTime.format(format);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getEndDateTime() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        return endDateTime.format(format);
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStartDateTime(String startDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        this.startDateTime = LocalDateTime.parse(startDateTime, formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEndDateTime(String endDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        this.endDateTime = LocalDateTime.parse(endDateTime, formatter);
    }

    public StudentEvent(
            String eventName,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.eventName = eventName;
    }

}
