package com.example.technovation2021;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class StudentActivity<adapter2, simple_spinner_dropdown_item> extends AppCompatActivity  {

    private static final String LOG_TAG = StudentActivity.class.getSimpleName();
    //private Spinner evDuration;
    private Spinner actFrequency; //How frequent the activity is (eg: Never, 1 week, 2 weeks, Month)
    private EditText actName; //Name of Activity
    private TextView actSDate; //Start Date of Activity
    private TextView actSTime; //Time the activity starts
    private TextView actETime; //Time the activity ends
    private TextView actNote;
    // private Spinner hwPrepTimes; //TODO: Delete
    // private SwitchCompat hwSwitch; //TODO: Delete
    //public EditText activityDes;
    public Button sunday;
    public Button monday;
    public Button tuesday;
    public Button wednesday;
    public Button thursday;
    public Button friday, saturday;
    Spinner rec;
    private int [] daysSelected = new int[7];
    //private Spinner evFrequency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_activity);

        // evDuration = findViewById(R.id.event_duration_times); //TODO: Delete
        //hwPrepTimes = findViewById(R.id.idHomeworkHours); //TODO: Delete
        //hwSwitch = findViewById(R.id.idSchoolTaskSwitch); //TODO: Delete
        actName = findViewById(R.id.idActName);
        //actSDate = findViewById(R.id.idActStartDate);
        //actSTime = findViewById(R.id.idActStartTime);
        //actETime = findViewById(R.id.idActEndTime);
        actFrequency = findViewById(R.id.idActRecurrence);
        //evFrequency = findViewById(R.id.event_frequency);
        rec = findViewById(R.id.idActRecurrence);
        actNote = findViewById(R.id.idActivityNote);

        sunday = findViewById(R.id.btnSunday);
        monday = findViewById((R.id.btnMonday));
        tuesday = findViewById((R.id.btnTuesday));
        wednesday = findViewById(R.id.btnWednesday);
        thursday = findViewById(R.id.btnThursday);
        friday = findViewById(R.id.btnFriday);
        saturday = findViewById(R.id.btnSaturday);

        sunday.setBackgroundColor(Color.WHITE);
        monday.setBackgroundColor(Color.WHITE);
        tuesday.setBackgroundColor(Color.WHITE);
        wednesday.setBackgroundColor(Color.WHITE);
        thursday.setBackgroundColor(Color.WHITE);
        friday.setBackgroundColor(Color.WHITE);
        saturday.setBackgroundColor(Color.WHITE);



        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x1 = (ColorDrawable) sunday.getBackground();
                if ( x1.getColor() == Color.WHITE ) {
                    sunday.setBackgroundColor( Color.CYAN);
                    // DayOfWeek.SUNDAY is 7. that is index 6 in daysSelected[]
                    daysSelected[6] = 1;
                }
                else {
                    daysSelected[6] = 0;
                    sunday.setBackgroundColor( Color.WHITE);
                }
            }
        });
        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x2 = (ColorDrawable) monday.getBackground();
                if ( x2.getColor() == Color.WHITE ) {
                    monday.setBackgroundColor( Color.CYAN);
                    daysSelected[0] = 1;
                }
                else {
                    monday.setBackgroundColor( Color.WHITE);
                    daysSelected[0] = 0;
                }
            }
        });
        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x3 = (ColorDrawable) tuesday.getBackground();
                if ( x3.getColor() == Color.WHITE ) {
                    tuesday.setBackgroundColor( Color.CYAN);
                    daysSelected[1] = 1;
                }
                else {
                    tuesday.setBackgroundColor( Color.WHITE);
                    daysSelected[1] = 0;
                }
            }
        });
        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x4 = (ColorDrawable) wednesday.getBackground();
                if ( x4.getColor() == Color.WHITE ) {
                    wednesday.setBackgroundColor( Color.CYAN);
                    daysSelected[2] = 1;
                }
                else {
                    wednesday.setBackgroundColor( Color.WHITE);
                    daysSelected[2] = 0;
                }
            }
        });
        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x5 = (ColorDrawable) thursday.getBackground();
                if ( x5.getColor() == Color.WHITE ) {
                    thursday.setBackgroundColor( Color.CYAN);
                    daysSelected[3] = 1;
                }
                else {
                    thursday.setBackgroundColor( Color.WHITE);
                    daysSelected[3] = 0;
                }
            }
        });
        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x6 = (ColorDrawable) friday.getBackground();
                if ( x6.getColor() == Color.WHITE ) {
                    friday.setBackgroundColor( Color.CYAN);
                    daysSelected[4] = 1;
                }
                else {
                    friday.setBackgroundColor( Color.WHITE);
                    daysSelected[4] = 0;
                }
            }
        });
        saturday.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                ColorDrawable x7 = (ColorDrawable) saturday.getBackground();
                if ( x7.getColor() == Color.WHITE ) {
                    //int c1 = 0x6dedd1;
                    saturday.setBackgroundColor(Color.CYAN);
                    daysSelected[5] = 1;
                }
                else {
                    saturday.setBackgroundColor( Color.WHITE);
                    daysSelected[5] = 0;
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_frequency, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        actFrequency.setAdapter(adapter);

    }

/*
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_duration_times, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        evDuration.setAdapter(adapter);

*/
//        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
//                R.array.event_frequency, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        adapter2.setDropDownViewResource(simple_spinner_dropdown_item);
//        // Apply the adapter to the spinner
//        actFrequency.setAdapter(adapter2);
/*
        ArrayAdapter<CharSequence> hwSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.hw_hours, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        hwPrepTimes.setAdapter(hwSpinnerAdapter);

        hwSwitch.setOnCheckedChangeListener(this);
        */




    /*
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
 */
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       /* switch (buttonView.getId()) {
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
        */
//    }


    public void startTimeClicked(View view) {
        DialogFragment newFragmentSTime = new TimePickerFragment(R.id.idActStartTime);
        newFragmentSTime.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "start time clicked");
    }

    public void endTimeClicked(View view) {
        DialogFragment newFragmentETime = new TimePickerFragment(R.id.idActEndTime);
        newFragmentETime.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "end time clicked");
    }

    public void startDateClicked(View view) {
        DialogFragment newFragment = new DatePickerFragment(R.id.idActStartDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addToCalendarClicked(View view) {
        if ( noDataErrors() ) {
            FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
            int ret = frd.createExtraCurricularActivity(actSTime.getText().toString(),
                    actETime.getText().toString(), actSDate.getText().toString(),
                    actName.getText().toString(), daysSelected, rec.getSelectedItemId(),
                    actNote.getText().toString().trim());
            if ( ret == 0 ) {
                Toast.makeText(StudentActivity.this, "Activity is added to Calendar!", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
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
    private boolean invalidStartTime(String dateInp, String timeStart) {
        Log.d(LOG_TAG, "dateInp " + dateInp + " timeStart: " + timeStart);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate inputDate = LocalDate.parse(dateInp, formatter);
        LocalDate today = LocalDate.now();
        if ( today.isBefore(inputDate) == true )
            return false;

        LocalTime lt = LocalTime.now();
        LocalTime timeInp = strTimeToLocalTime(timeStart);
        return timeInp.isBefore(lt) == true;
    }

    private boolean noDaysAreSelected() {
        int nrDaysSelected = 0;
        for ( int i = 0; i<= 6; i++ ) {
            Log.d(LOG_TAG, "index " + i + " selected? " + daysSelected[i]);
            if ( daysSelected[i] == 1)
                nrDaysSelected++;
        }
        Log.d(LOG_TAG, "total days selected: " + nrDaysSelected);
        if ( nrDaysSelected == 0 )
            return true;
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean noDataErrors() {
        actName = findViewById(R.id.idActName);
        actSDate = findViewById(R.id.idActStartDate);
        //Log.d("Startdateabc", "start date picked " + actSDate );
        actSTime = findViewById(R.id.idActStartTime);
        actETime = findViewById(R.id.idActEndTime);
        actFrequency = findViewById(R.id.idActRecurrence);
        sunday = findViewById(R.id.btnSunday);
        monday = findViewById((R.id.btnMonday));
        tuesday = findViewById((R.id.btnTuesday));
        wednesday = findViewById(R.id.btnWednesday);
        thursday = findViewById(R.id.btnThursday);
        friday = findViewById(R.id.btnFriday);
        saturday = findViewById(R.id.btnSaturday);


        // Do sanity checks to make sure data is good!
        if (TextUtils.isEmpty(actName.getText().toString()) ||
            actName.getText().toString().length() < 5) {
            actName.setError("Too short description.");
            Toast.makeText(StudentActivity.this, "Description is too short.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(actSDate.getText().toString()) ) {
            actSDate.setError("Please input a date");
            //Toast.makeText(StudentActivity.this, "Input date", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d(LOG_TAG, "check start date: " + actSDate.getText().toString());
        if ( invalidStartDate(actSDate.getText().toString()) == true ) {
            actSDate.setError("Start date cannot be earlier than today.");
            return false;
        }
        actSDate.setError(null);

        if (TextUtils.isEmpty(actSTime.getText().toString()) ) {
            actSTime.setError("Please input a start time");
            //Toast.makeText(StudentActivity.this, "Input start time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ( invalidStartTime(actSDate.getText().toString(), actSTime.getText().toString()) == true ) {
            actSTime.setError("Start time has already passed.");
            return false;
        }
        actSTime.setError(null);

        if (TextUtils.isEmpty(actETime.getText().toString()) ) {
            actETime.setError("Please input an end time");
            //Toast.makeText(StudentActivity.this, "Input end time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (time2IsAfterTime1(actETime.getText().toString(), actSTime.getText().toString()) == false) {
            Toast.makeText(StudentActivity.this, "End activity time cannot be earlier than start time.", Toast.LENGTH_LONG).show();
            return false;
        }

        ColorDrawable x1 = (ColorDrawable) sunday.getBackground();

        if ( noDaysAreSelected() == true ) {
            Toast.makeText(StudentActivity.this, "Day of activity is not selected.", Toast.LENGTH_LONG).show();
            return false;
        }
        actETime.setError(null);

        //Log.d(LOG_TAG, "recur: " + rec.getSelectedItemId());
        //Toast.makeText(StudentActivity.this, "ALL GOOD!!", Toast.LENGTH_SHORT).show();
        /*
        if (TextUtils.isEmpty(actFrequency.getText().toString()) ) {
            actFrequency.setError("Please input the recurrence");
            Toast.makeText(StudentActivity.this, "Input recurrence", Toast.LENGTH_SHORT).show();
            return false;
        }*/ //TODO: delete
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalTime strTimeToLocalTime(String userTime) {
        int hr = 0, min = 0;
        try {
            hr = Integer.parseInt(userTime.split(" ")[0].split(":")[0]);
            min = Integer.parseInt(userTime.split(" ")[0].split(":")[1]);
            if (userTime.contains("AM") && hr == 12) {
                hr -= 12;
            }
            if (userTime.contains("PM") && hr >= 1 && hr <= 11) {
                hr += 12;
            }
        } catch (Exception e) {

        }
        return LocalTime.of(hr, min);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Boolean time2IsAfterTime1(String time1, String time2) {
        LocalTime t1 = strTimeToLocalTime(time1);
        LocalTime t2 = strTimeToLocalTime(time2);
        // time is in format hh:mm am or pm
        // if t1 > t2 > 0
        if (t1.compareTo(t2) > 0) {
            Log.d(LOG_TAG, time1 + " is gt " + time2);
            return true;
        }
        return false;
    }


    public void cancelAddActivityClicked(View view) {
        //super.onBackPressed();
        finish();
    }
}
