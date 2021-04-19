package com.example.technovation2021;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.Period;
import java.util.Date;
import java.util.Calendar;

public class AddTask extends AppCompatActivity {

    private static final String LOG_TAG = AddTask.class.getSimpleName();

    private EditText taskName; //Name of Task
    private TextView taskSDate; //Start Date of Task
    private TextView taskDDate; //Due Date of Task
    private EditText taskDuration; //Duration of Task
    private EditText taskNote; //Duration of Task
    private EditText taskIdealSitting; //Ideal time user will spend on the task in 1 sitting

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskName = findViewById(R.id.idTaskName);
        taskSDate = findViewById(R.id.idTaskStartDate);
        taskDDate = findViewById(R.id.idTaskDueDate);
        taskNote = findViewById(R.id.idTaskNotes);
    }

    public void startDateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment(R.id.idTaskStartDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void dueDateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment(R.id.idTaskDueDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean invalidStartDate(String dateInp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        try {
            LocalDate inputDate = LocalDate.parse(dateInp, formatter);
            Log.d(LOG_TAG, "input date:" + inputDate.toString());
            LocalDate today = LocalDate.now();
            return inputDate.isBefore(today) == true;
            //Log.d(LOG_TAG, "date check: " + chk + "date converted: " + inputDate.toString());
            //return chk;
        } catch (Exception e) {
            Log.d(LOG_TAG, "input date:" + dateInp + "exception:" + e.toString());
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean invalidEndDate(String inpstartDate, String inpendDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        try {
            LocalDate startDate = LocalDate.parse(inpstartDate, formatter);
            LocalDate endDate = LocalDate.parse(inpendDate, formatter);
//            Log.d(LOG_TAG, "input date:" + inputDate.toString());
//            LocalDate today = LocalDate.now();
            return endDate.isBefore(startDate) == true;
            //Log.d(LOG_TAG, "date check: " + chk + "date converted: " + inputDate.toString());
            //return chk;
        } catch (Exception e) {
            //Log.d(LOG_TAG, "input date:" + dateInp + "exception:" + e.toString());
        }
        return true;
    }

    // Convert date in "MM/DD/YYYY" format to "yyyy-MM-dd"
    @RequiresApi(api = Build.VERSION_CODES.O)
    private String dateToStr(String d) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate t = LocalDate.parse(d, formatter);
        return t.toString();
    }

    public void taskAddedToCalendar() {
        Toast.makeText(AddTask.this, "Task is added successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addToCalendarClicked(View view) {
        if (noDataErrors()) {
            Log.d("noDataErrors", "StartDateTime= " + taskSDate.getText().toString());
            Log.d("noDataErrors", "EndDateTime= " + taskDDate.getText().toString());


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
            taskName = findViewById(R.id.idTaskName);
            String strTaskName = taskName.getText().toString(); //Task Name as a string

            taskSDate = findViewById(R.id.idTaskStartDate);

            String notes = taskNote.getText().toString();
            if ( notes.isEmpty() )
                notes = "NONE";
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            GenericTask newtask = new GenericTask(String.valueOf(System.currentTimeMillis()),
                    dateToStr(taskSDate.getText().toString()),
                    dateToStr(taskDDate.getText().toString()),
                    taskName.getText().toString(), notes,
                    String.valueOf(System.currentTimeMillis()), 2, 120);
            frd.saveHwTask(newtask, this, -1);
        }
    }

        private void getDayEvents (LocalDateTime ldt){
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private boolean noDataErrors () {

            if (TextUtils.isEmpty(taskName.getText().toString())) {
                taskName.setError("Task name cannot be empty");
                Toast.makeText(AddTask.this, "Task name cannot be empty", Toast.LENGTH_SHORT).show();
                return false;
            }
            taskName.setError(null);

            if (TextUtils.isEmpty(taskSDate.getText().toString())) {
                taskSDate.setError("Please input a date");
                Toast.makeText(AddTask.this, "Please input a date.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if ( invalidStartDate(taskSDate.getText().toString()) == true ) {
                Toast.makeText(AddTask.this, "Start date cannot be earlier than today.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (TextUtils.isEmpty(taskDDate.getText().toString())) {
                taskDDate.setError("Please input a date");
                Toast.makeText(AddTask.this, "Please input a date.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if ( invalidStartDate(taskDDate.getText().toString()) == true ) {
                Toast.makeText(AddTask.this, "Due date cannot be earlier than today.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if ( invalidEndDate(taskSDate.getText().toString(), taskDDate.getText().toString()) == true ) {
                Toast.makeText(AddTask.this, "Due date cannot be earlier than start date.", Toast.LENGTH_SHORT).show();
                return false;
            }

//            SimpleDateFormat s1 = new SimpleDateFormat("mm/dd/yyyy");
//            try {
//                Date tSDate = s1.parse(taskSDate.getText().toString());
//                Date tDDate = s1.parse(taskDDate.getText().toString());
//                if (tDDate.compareTo(tSDate) < 0) {
//                    Toast.makeText(AddTask.this, "Due date can not be before start date. ", Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            } catch (Exception e) {
//
//            }
            return true;
        }

        public void cancelAddActivityClicked (View view){
            finish();
        }
    }