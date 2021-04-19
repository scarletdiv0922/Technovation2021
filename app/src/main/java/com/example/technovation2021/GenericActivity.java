package com.example.technovation2021;

import android.os.Build;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;

// Use this object for telling app about school, extra curricular activities (like piano,
// volleyball, volunteer hours), dinner, school club meetings etc
public class GenericActivity implements Serializable, Comparable {
    LocalDate startDate;
    LocalDate endDate;
    LocalTime startTime;
    int duration;
    int repeats;
    String desc;
    int activityId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GenericActivity(String startDate,
                           String endDate,
                           String startTime,
                           int _duration,
                           int repeats,
                           Integer activityId,
                           String desc) {
        this.startDate = strToDate(startDate);
        this.endDate = strToDate(endDate);
        this.startTime = strToTime(startTime);
        this.duration = _duration;
        this.repeats = repeats;
        this.desc = desc;
        this.activityId = activityId;
    }

    public int getRepeats() {
        return repeats;
    }
    public void setRepeats(int repeats) {
        this.repeats = repeats;
    }
    public Integer getActivityId() {
        return activityId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateToString(LocalDate d) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return d.format(formatter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setActivityId(Integer x) {
        this.activityId = x;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getStartDate() {
        return dateToString(startDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStartDate(String startDate) {

        this.startDate = LocalDate.parse(startDate);//strToDate(startDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getEndDate() {
        return dateToString(endDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEndDate(String endDate) {
        this.endDate = strToDate(endDate);
    }

    public String getStartTime() {
        return startTime.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate strToDate(String dateStr) {
        return LocalDate.of(
                Integer.parseInt(dateStr.split("-")[0]), // year
                Integer.parseInt(dateStr.split("-")[1]), // month
                Integer.parseInt(dateStr.split("-")[2]));
    }

    // Convert time to 24 hour format.
    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalTime strToTime(String tmStr) {
        // tmStr may contain AM or PM
        // expected tmStr format "12:28 AM"
        int hr = Integer.parseInt(tmStr.split(":")[0]);
        int mn = Integer.parseInt(tmStr.split(" ")[0].split(":")[1]);
        if ( tmStr.contains("AM") && hr == 12 )
            hr -= 12;
        if ( tmStr.contains("PM") && (hr >= 1 && hr <= 11) )
            hr += 12;
        return LocalTime.of(hr,mn, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStartTime(String startTime) {
        this.startTime = strToTime(startTime);
    }

    public int getDuration() {
        return duration;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDuration(int d) {
        this.duration = d;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String print() {
        return "desc " + getDesc() + " starttime " + getStartDate() + " repeats " + repeats + " enddate " + getEndDate() +
                " duration " + duration + " activityId " + activityId;
    }

    public DayOfWeek dayOfWeek() {
        return startDate.getDayOfWeek();
    }

    public GenericActivity() {

    }

    @Override
    public int compareTo(Object o) {
        return this.startTime.compareTo(((GenericActivity)o).startTime);
    }
}
