package com.example.technovation2021;

import android.os.Build;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;

public class Event implements Serializable {
    // Event start date, start time.
    public LocalDateTime startDateTime;
    // Event end date, end time
    public LocalDateTime endDateTime;
    public Integer activityDuration;
    public Integer recurs;
    public Boolean schoolTask;
    public Integer timeToFinish;
    public String notes;
    public String eventDesc;

    public Boolean getSchoolTask() {
        return schoolTask;
    }

    public Integer getRecurs() {
        return recurs;
    }

    public Integer getActivityDuration() {
        return activityDuration;
    }

    public String getEndDateTime() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        return endDateTime.format(format);
    }

    public String getStartDateTime() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        return startDateTime.format(format);
    }

    public Integer getTimeToFinish() {
        return timeToFinish;
    }

    public String getNotes() {
        return notes;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEndDateTime(String endDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        this.endDateTime = LocalDateTime.parse(endDateTime, formatter);
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setRecurs(Integer recurs) {
        this.recurs = recurs;
    }

    public void setSchoolTask(Boolean schoolTask) {
        this.schoolTask = schoolTask;
    }

    public void setStartDateTime(String startDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        this.startDateTime = LocalDateTime.parse(startDateTime, formatter);
    }

    public void setTimeToFinish(Integer timeToFinish) {
        this.timeToFinish = timeToFinish;
    }

    public void setActivityDuration(Integer activityDuration) {
        this.activityDuration = activityDuration;
    }

    public String print() {

        return getEventDesc() + " Start:" + getStartDateTime() + " " + getNotes() + " End:" + getEndDateTime() +
                " Duration:" + getActivityDuration() + " Recurs:" + getRecurs() + " HoursToFinish:" + getTimeToFinish() +
                " SchoolTask:" + getSchoolTask();
    }

    public Event() {

    }

    public Event(
            String eventDesc,
            String startDateTime,
            String endDateTime,
            Integer activityDuration,
            Integer recurs,
            Boolean schoolTask,
            Integer timeToFinish,
            String notes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        this.startDateTime = LocalDateTime.parse(startDateTime, formatter);
        this.endDateTime = LocalDateTime.parse(endDateTime, formatter);
        this.eventDesc = eventDesc;
        this.activityDuration = activityDuration;
        this.recurs = recurs;
        this.timeToFinish = timeToFinish;
        this.notes = notes;
        this.schoolTask = schoolTask;
    }
}
