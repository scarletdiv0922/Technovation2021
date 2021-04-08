package com.example.technovation2021;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText taskIdealSitting; //Ideal time user will spend on the task in 1 sitting

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
    }

    public void startDateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment(R.id.idTaskStartDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void dueDateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment(R.id.idTaskDueDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void addToCalendarClicked(View view) {
        if ( noDataErrors() ) {
            taskName = findViewById(R.id.idTaskName);
            taskSDate = findViewById(R.id.idTaskStartDate);
            taskDDate = findViewById(R.id.idTaskDueDate);
            taskDuration = findViewById(R.id.idTaskDuration);
            taskIdealSitting = findViewById(R.id.idTaskIdealSitting);

            int sittings= Integer.parseInt(taskDuration.getText().toString())/Integer.parseInt(taskIdealSitting.getText().toString());
           Log.d("noDataErrors", "sittings= "+sittings);

            int remainder= (Integer.parseInt(taskDuration.getText().toString()))%(Integer.parseInt(taskIdealSitting.getText().toString()));

            Log.d("noDataErrors", "remainder= "+remainder);

            Log.d("noDataErrors", "StartDateTime= "+taskSDate.getText().toString());
            Log.d("noDataErrors", "EndDateTime= "+taskDDate.getText().toString());


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
            taskName = findViewById(R.id.idTaskName);
            String strTaskName= taskName.getText().toString(); //Task Name as a string

            taskSDate = findViewById(R.id.idTaskStartDate);

       //     StudentEvent se = new StudentEvent(strTaskName, startDateTime, endDateTime);

            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
       //     frd.saveCalendarEvent("eventList", se);

            finish();
        }
    }

    private void getDayEvents (LocalDateTime ldt){
        FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
    //    frd.getDayEvents("eventList", ldt);
    }

    private boolean noDataErrors() {
        taskName = findViewById(R.id.idTaskName);
        taskSDate = findViewById(R.id.idTaskStartDate);
        taskDDate = findViewById(R.id.idTaskDueDate);
        taskDuration = findViewById(R.id.idTaskDuration);
        taskIdealSitting = findViewById(R.id.idTaskIdealSitting);

        int tDuration; //Duration of task as integer
        int tIdealSitting; //Ideal Sitting of task as integer
     //   LocalDateTime currentDate= LocalDateTime.now();
   //     Date tSDate = new Date(2021, 3, 24); //Start Date for task in date format //TODO: Remove comment
     //   Date tDDate = new Date(2021, 3, 24); //Due Date for task in date format //TODO: Remove Comment

        //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm/dd/yyyy");
       // LocalDateTime now = LocalDateTime.now();

   /*     //TODO: make sure that start date is on or after current date
        SimpleDateFormat s1 = new SimpleDateFormat ("mm/dd/yyyy");
        try {
             tSDate = s1.parse(taskSDate.getText().toString());
             tDDate = s1.parse(taskDDate.getText().toString());
            if (tDDate.compareTo(tSDate) <0){
                Toast.makeText(AddTask.this, "Due date can not be before start date", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch (Exception e){

        }
//TODO: Remove comment from line 123-137
*/
        if (TextUtils.isEmpty(taskName.getText().toString()) ) {
            taskName.setError("Please input a name");
            Toast.makeText(AddTask.this, "Input name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(taskDuration.getText().toString()) ) {
            taskDuration.setError("Please input a duration");
            Toast.makeText(AddTask.this, "Input duration", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            tDuration = Integer.parseInt(taskDuration.getText().toString());
        }
        catch (Exception e){
            taskDuration.setError("Please input a valid number");
            Toast.makeText(AddTask.this, "Input valid duration", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tDuration <= 0) {
            taskDuration.setError("Please input a number greater than zero");
            Toast.makeText(AddTask.this, "Input duration greater than zero", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(taskIdealSitting.getText().toString()) ) {
            taskIdealSitting.setError("Please input an ideal sitting");
            Toast.makeText(AddTask.this, "Input ideal sitting", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            tIdealSitting = Integer.parseInt(taskIdealSitting.getText().toString());
        }
        catch (Exception e){
            taskIdealSitting.setError("Please input a valid number");
            Toast.makeText(AddTask.this, "Input valid maximum sitting", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tIdealSitting <= 0) {
            taskIdealSitting.setError("Please input a number greater than zero");
            Toast.makeText(AddTask.this, "Input a maximum sitting greater than zero", Toast.LENGTH_SHORT).show();
            return false;
        }

/*
        if (TextUtils.isEmpty(actSDate.getText().toString()) ) {
            taskName.setError("Please input a date");
            Toast.makeText(StudentActivity.this, "Input date", Toast.LENGTH_SHORT).show();
            return false;
        }
*/

        return true;
    }

    public void cancelAddActivityClicked(View view) {
        finish();
    }
}