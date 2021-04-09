package com.example.technovation2021;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;

public class Event implements Serializable, Comparable {
    // Event start date, start time.
    LocalDate date;
    LocalTime startTime;
    // Event end date, end time
    //LocalDateTime endDateTime;
    Integer duration;
    // If a task gets divided into, say, 5 working slots, which one
    // is this particular slot.
    Integer chunkNumber = 0;
    Integer totalChunks;
    // break = 1, homework = 2, extra-curricular activity = 3, do-not-disturb = 4
    // by default do not show event type 4: school, dinner, sleep at home events in daily view.
    Integer type;

    //public Integer activityDuration;
    //public Integer recurs;
    //public Boolean schoolTask;
    //public Integer timeToFinish;

    // link to "master" activity or task
    String taskId;
    String notes;
    String eventDesc;

    /*

            Integer activityDuration,
            Integer recurs,
            Boolean schoolTask,
            Integer timeToFinish,

    public Boolean getSchoolTask() {
        return schoolTask;
    }

    public Integer getRecurs() {
        return recurs;
    }

    public Integer getActivityDuration() {
        return activityDuration;
    }

    public Integer getTimeToFinish() {
        return timeToFinish;
    }

    public void setRecurs(Integer recurs) {
        this.recurs = recurs;
    }

    public void setSchoolTask(Boolean schoolTask) {
        this.schoolTask = schoolTask;
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

     */

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String task) {
        taskId = task;
    }

/*
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getStartDateTime() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return startDateTime.format(format);
    }

 */

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(int nr) {
        chunkNumber = nr;
    }

    public Integer getTotalChunks() { return totalChunks; }
    public void setTotalChunks(Integer totalChunks1 ) {
        totalChunks = totalChunks1;
    }
    public Integer getType() {
        return type;
    }
    public String getNotes() {
        return notes;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setType(Integer _type) {
        this.type = _type;
    }

    public void setDuration(Integer _duration) {
        duration = _duration;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setDate(String date) {
        this.date = LocalDate.parse(date);
    }

    public String getDate() {
        return date.toString();
    }

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
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        //this.startDateTime = LocalDateTime.parse(startDateTime, formatter);
        this.startTime = strToTime(startTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String print() {
        return "EventObj: Date:" + date.toString() + " Start:" + getStartTime() + " " + getEventDesc() + " " + getNotes() + " Duration: " + getDuration();
    }

    public String getStartTime() {
        return this.startTime.toString();
    }

    public Event() {

    }

    /*
        LocalDate date;
    LocalTime startTime;
    // Event end date, end time
    //LocalDateTime endDateTime;
    Integer duration;
    // break = 1, homework = 2, activity = 3
    Integer type;

    String taskId;
    String notes;
    String eventDesc;

     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Event(
            String eventDesc,
            String date,
            String startTime,
            Integer chunkNumber,
            Integer totalChunks,
            Integer duration,
            Integer type,
            String taskId,
            String notes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        this.eventDesc = eventDesc;
        this.date = LocalDate.parse(date);
        //LocalDateTime.parse(startDateTime, formatter);
        this.startTime = LocalTime.parse(startTime);
        this.notes = notes;
        this.taskId = taskId;
        this.duration = duration;
        this.chunkNumber = chunkNumber;
        this.totalChunks = totalChunks;
        this.type = type;
        /*
        this.activityDuration = activityDuration;
        this.recurs = recurs;
        this.timeToFinish = timeToFinish;
        this.schoolTask = schoolTask;
         */
    }

    //public String print() {
        //return "EventObj: " + eventDesc + " " + date.toString() + " Time: " + startTime.toString() + " duration: " + duration;
    //}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compareTo(Object o) {
        return startTime.compareTo(((Event)o).startTime);
    }

    /*
    @Override
    public int describeContents() {
        return 0;
    }

     */

    /*
    LocalDate date;
    LocalTime startTime;
    // Event end date, end time
    //LocalDateTime endDateTime;
    Integer duration;
    // If a task gets divided into, say, 5 working slots, which one
    // is this particular slot.
    Integer chunkNumber = 0;
    Integer totalChunks;
    // break = 1, homework = 2, activity = 3, do-not-disturb = 4
    Integer type;
    // link to "master" activity or task
    String taskId;
    String notes;
    String eventDesc;

            String eventDesc,
            String date,
            String startTime,
            Integer chunkNumber,
            Integer totalChunks,
            Integer duration,
            Integer type,
            String taskId,
            String notes


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.d("EventParcel", Integer.toString(i));
        parcel.writeString(eventDesc);
        parcel.writeString( date.toString());
        parcel.writeString( startTime.toString());
        parcel.writeInt(chunkNumber);
        parcel.writeInt(totalChunks);
        parcel.writeInt(duration);
        parcel.writeInt(type);
        parcel.writeString(taskId);
        parcel.writeString(notes);
    }

     */
}
