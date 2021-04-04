package com.example.technovation2021;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

public class AddActivity extends AppCompatActivity implements
        CompoundButton.OnCheckedChangeListener {

    private static final String LOG_TAG = AddActivity.class.getSimpleName();
    private Spinner evDuration;
    private Spinner evFrequency;
    private Spinner hwPrepTimes;
    private SwitchCompat hwSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity);

        evDuration = findViewById(R.id.event_duration_times);
        evFrequency = findViewById(R.id.event_frequency);
        hwPrepTimes = findViewById(R.id.idHomeworkHours);
        hwSwitch = findViewById(R.id.idSchoolTaskSwitch);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_duration_times, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        evDuration.setAdapter(adapter);


        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.event_frequency, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        evFrequency.setAdapter(adapter2);

        ArrayAdapter<CharSequence> hwSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.hw_hours, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        hwPrepTimes.setAdapter(hwSpinnerAdapter);

        hwSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideHomeworkData();
        hwSwitch.setChecked(false);
    }
    
    public void getHomeworkData() {
        findViewById(R.id.idTxtHomeworkHours).setVisibility(View.VISIBLE);
        findViewById(R.id.idHomeworkHours).setVisibility(View.VISIBLE);
        findViewById(R.id.idTxtSubmitBy).setVisibility(View.VISIBLE);
        findViewById(R.id.idSubmissionDate).setVisibility(View.VISIBLE);
    }

    public void hideHomeworkData() {
        findViewById(R.id.idTxtHomeworkHours).setVisibility(View.GONE);
        findViewById(R.id.idHomeworkHours).setVisibility(View.GONE);
        findViewById(R.id.idTxtSubmitBy).setVisibility(View.GONE);
        findViewById(R.id.idSubmissionDate).setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.idSchoolTaskSwitch:
                Log.d("switch_compat", isChecked + "");
                if ( isChecked ) {
                    getHomeworkData();
                }
                else {
                    hideHomeworkData();
                }
                break;
        }
    }

    public void startTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "start time clicked");
    }

    public void startDateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment(R.id.idEventStartDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void submissionDateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment(R.id.idSubmissionDate);
        newFragment.show(getSupportFragmentManager(), "submissionDatePicker");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addToCalendarClicked(View view) {
        if ( noDataErrors() ) {
            //fb.saveCalendarEvent();
            // Saving of event to firebase db

            /*
            Event e = new Event("Test event",
                    "03-05-2021 12:12",
                    "06-28-2021 11:23",
                    245,
                    0, false, 145, "test event notes");
            //mDatabase.child(userId).child(userKey).setValue(sld);
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            frd.saveCalendarEvent("activityList", e);
*/

/*
            String eventDesc,
            String startDateTime,
            String endDateTime,
            Integer duration,
            Integer type,
            String taskId,
            String notes
 */

            /*

            Event e = new Event("Test event",
                    "2021-04-01 12:12",
                    "2022-05-05 11:23",
                    245,
                    0, "1234", "test event notes");
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            frd.saveCalendarEvent("activityList", e);

             */

            /*
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            frd.getAllActivities("activityList");

             */

            /* Add some activities for test
            // 1 - One time. check that startDate is later than today
            // 2 - Weekly on same day as start day
            // 3 - Once every 2 weeks on same day as start day
            // 4 - Same day next month
            // 5 - all weekdays
            // 6 - once an year on same date as start date.
            * */


/*
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();

            GenericActivity a = new GenericActivity("2021-04-01", "2022-12-12",
                    "08:30 AM", 420, 5, 12354, "school");
            frd.saveActivity(a);

            GenericActivity b = new GenericActivity("2021-04-15", "2022-10-12",
                    "5:15 PM", 60, 2, 123456, "piano");
            frd.saveActivity(b);

            GenericActivity c = new GenericActivity("2021-04-12", "2021-10-12",
                    "7:45 PM", 90, 2, 8944554, "volleyball");
            frd.saveActivity(c);

            GenericActivity d = new GenericActivity("2021-04-15", "2021-10-12",
                    "7:45 PM", 90, 2, 75341, "volleyball");
            frd.saveActivity(d);

            GenericActivity e = new GenericActivity("2021-04-13", "2021-10-12",
                    "4:00 PM", 90, 3, 32456, "volunteer");
            frd.saveActivity(e);

            GenericActivity f = new GenericActivity("2021-04-01", "2031-10-12",
                    "12:01 AM", 500, 7, 3277456, "SLEEPAM");
            frd.saveActivity(f);

            GenericActivity g = new GenericActivity("2021-04-01", "2031-10-12",
                    "10:00 PM", 120, 7, 3277456, "SLEEPPM");
            frd.saveActivity(g);


 */

            //GenericActivity f = new GenericActivity("2021-04-17", "2021-10-12",
                    //"2:00 PM", 180, 4, Long.valueOf(1288567899), "social");
            //frd.saveActivity(f);



            /*
            String taskId,
                       String startDate,
                       String dueDate,
                       String desc,
                       String notes,
                       String hash,
                       Integer status,
                       Integer hoursNeeded
             */


            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            GenericTask t1 = new GenericTask("TASK1234",
                    "2021-04-03", "2021-04-04",
                    "math hw", "algebra", "HASH1234",
                    0, 0);
            frd.saveTask(t1);


            GenericTask t2 = new GenericTask("TASK12345",
                    "2021-04-03", "2021-04-06",
                    "PE hw", "jumpin jacks", "HASH123456",
                    0, 0);
            frd.saveTask(t2);

            GenericTask t3 = new GenericTask("TASK12345",
                    "2021-04-04", "2021-04-09",
                    "Science", "build event", "HASH987",
                    0, 0);

            frd.saveTask(t3);

        }
    }

    private boolean noDataErrors() {
        return true;
    }

    public void cancelAddActivityClicked(View view) {
        //super.onBackPressed();
        finish();
    }
}