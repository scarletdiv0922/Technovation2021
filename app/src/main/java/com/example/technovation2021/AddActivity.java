package com.example.technovation2021;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

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


    public void addToCalendarClicked(View view) {
        if ( noDataErrors() ) {
            //fb.saveCalendarEvent();
            // Saving of event to firebase db
            /*
            Event e = new Event("Test event", "03-25-2021 11:12",
                    "04-11-2021 11:23", "45",
                    "0", false, "45", "test event notes");
            //mDatabase.child(userId).child(userKey).setValue(sld);
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            frd.saveCalendarEvent("activityList", e);
            */
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            frd.getAllActivities("activityList");
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