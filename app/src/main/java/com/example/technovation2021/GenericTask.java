package com.example.technovation2021;

import android.os.Build;
import android.util.Log;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;

public class GenericTask implements Serializable {
    String taskId;
    LocalDate dueDate;
    LocalDate startDate;
    String desc;
    String notes;
    String hash;
    Integer status; // maybe 1 for unscheduled, 2 for scheduled, 3 for completed, 4 for cancelled??
    Integer timeNeeded; // Time needed in Minutes to complete whole task
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    public static final int MIN_TASK_TIME = 15;  // 15 mins;
    public static final int MIN_BREAK_TIME = 10; // 10 mins break time

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GenericTask(String taskId,
                       String startDate,
                       String dueDate,
                       String desc,
                       String notes,
                       String hash,
                       Integer status,
                       Integer timeNeeded
                       ) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        this.taskId = taskId;
        this.startDate = LocalDate.parse(startDate, formatter);
        this.dueDate = LocalDate.parse(dueDate, formatter);
        this.desc = desc;
        this.notes = notes;
        this.hash = hash;
        this.status = status;
        this.timeNeeded = timeNeeded;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDueDate() {
        return dueDate.toString();
    }

    public String print() {
        return hash + " " + startDate.toString() + " " + desc + " " + notes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDueDate(String dueDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.dueDate = LocalDate.parse(dueDate, formatter);
    }

    public String getStartDate() {
        return startDate.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setStartDate(String startDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.startDate = LocalDate.parse(startDate, formatter);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTimeNeeded() {
        return timeNeeded;
    }

    public void setHoursNeeded(Integer timeNeeded) {
        this.timeNeeded = timeNeeded;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int nrDaysToDue() {
        assert Period.between(startDate, dueDate).getDays() > 0;
        return Period.between(startDate, dueDate).getDays();
    }

    public GenericTask() {

    }

    // Time to finish the task without any break.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public int timeToFinishTheTask() {
        int timeToFinish = 10;
        // getDays() for "2021-04-02" to "2021-04-05" returns 3
        // subtract 1 so student can submit the homework a day before
        int daysToDueDate = Period.between(startDate, dueDate).getDays();
        Log.d(LOG_TAG, "daysToSubmit " + daysToDueDate);
        switch ( daysToDueDate ) {
            case 1: // 15x??
                timeToFinish = 15;
                break;
            case 2:
                timeToFinish = 30;
                break;
            case 3:
                timeToFinish = 45;
                break;
            case 4:
                timeToFinish = 90;
                break;
            case 5:
                timeToFinish = 120;
                break;
            case 6-10: // 3h
                timeToFinish = 180;
                break;
            default: // 6h
                timeToFinish = 360;
        }
        Log.d(LOG_TAG, "timetoFinish: " + timeToFinish);
        return timeToFinish;
    }
}
