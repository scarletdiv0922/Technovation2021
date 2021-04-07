package com.example.technovation2021;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.sql.Time;
import java.time.LocalTime;
import java.util.SimpleTimeZone;


public class UserInfo<mAuth, mDatabase> extends AppCompatActivity {
    private static final String LOG_TAG = AddActivity.class.getSimpleName();


    TextView startSchool;
    TextView endSchool;
    TextView startSleep;
    TextView endSleep;
    TextView startDinner;
    TextView endDinner;
    TextView startWkend;
    TextView endWkend;
    ProgressBar pbar;
    Button continueButton;

//    ProgressBar pbar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        startSchool = findViewById(R.id.startSchoolTime);
        endSchool = findViewById(R.id.endSchoolTime);
        startSleep = findViewById(R.id.startSleepTime);
        endSleep = findViewById(R.id.endSleepTime);
        startDinner = findViewById(R.id.startDinnerTime);
        endDinner = findViewById(R.id.endDinnerTime);
        startWkend = findViewById(R.id.startWkendSched);
        endWkend = findViewById(R.id.endWkendSched);
        continueButton = findViewById(R.id.btncontinue);
        pbar = findViewById(R.id.pbarUserInfo);



        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }


    //    time picker for start school time
    public void startSchoolTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.startSchoolTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "start school time clicked");
    }

    //    time picker for end school time
    public void endSchoolTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.endSchoolTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "end school time clicked");

    }

    //    time picker for start sleep time
    public void startSleepTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.startSleepTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "start sleep time clicked");
    }
    //    time picker for end sleep time

    public void endSleepTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.endSleepTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "end sleep time clicked");
    }

    public void startDinnerTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.startDinnerTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "start dinner time clicked");
    }


    public void endDinnerTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.endDinnerTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "end dinner time clicked");
    }


    public void startWkendSchedClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.startWkendSched);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "start wkend scheduling time clicked");
    }


    public void endWkendSchedClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.endWkendSched);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "end wkend scheduling time clicked");
    }


    // parse time in "hh:mm am" format
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


    public String getTextOf(TextView tv) {
        return tv.getText().toString();
    }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public void continueClicked(View view) {
            if (checksSuccessful()) {

                pbar.setVisibility(View.VISIBLE);

                FirebaseRealtimeDatabase frd = new FirebaseRealtimeDatabase();
                frd.createWeekdayEvent(getTextOf(startSchool), getTextOf(endSchool), "School");
                frd.createWeekdayEvent(getTextOf(startSleep), "11:59 PM", "Sleep");
                frd.createWeekdayEvent("12:01 AM", getTextOf(endSleep), "Sleep");
                frd.createEverydayEvent(getTextOf(startDinner), getTextOf(endDinner), "Dinner");
                frd.createWeekendEvent("12:01 AM", getTextOf(startWkend), "Weekend Family Time");
                frd.createWeekendEvent(getTextOf(endWkend), "11:59 PM", "Weekend Family Time");

                pbar.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(UserInfo.this, CalendarActivity.class);
                startActivity(intent);



            }
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
        public boolean checksSuccessful() {
            startSchool = findViewById(R.id.startSchoolTime);
            endSchool = findViewById(R.id.endSchoolTime);
            startSleep = findViewById(R.id.startSleepTime);
            endSleep = findViewById(R.id.endSleepTime);
            startDinner = findViewById(R.id.startDinnerTime);
            endDinner = findViewById(R.id.endDinnerTime);
            startWkend = findViewById(R.id.startWkendSched);
            endWkend = findViewById(R.id.endWkendSched);


            Log.d(LOG_TAG, "startschool " + startSchool.getText().toString());
            if (startSchool.getText().toString().isEmpty()) {
                startSchool.setError("Cannot be empty.");
                return false;
            } else startSchool.setError(null);

            //check if end school is empty
            if (endSchool.getText().toString().isEmpty()) {
                endSchool.setError("Cannot be empty.");
                return false;
            } else endSchool.setError(null);


            // check if start sleep is empty
            if (startSleep.getText().toString().isEmpty()) {
                startSleep.setError("Cannot be empty.");
                return false;
            } else startSleep.setError(null);

            //check if end sleep is empty
            if (endSleep.getText().toString().isEmpty()) {
                endSleep.setError("Cannot be empty.");
                return false;
            } else endSleep.setError(null);

            // check if start dinner is empty
            if (startDinner.getText().toString().isEmpty()) {
                startDinner.setError("Cannot be empty.");
                return false;
            } else startDinner.setError(null);

            //check if end dinner is empty
            if (endDinner.getText().toString().isEmpty()) {
                endDinner.setError("Cannot be empty.");
                return false;
            } else endDinner.setError(null);

            if (time2IsAfterTime1(endSchool.getText().toString(), startSchool.getText().toString()) == false) {
                Toast.makeText(UserInfo.this, "End school time cannot be earlier than start time.", Toast.LENGTH_SHORT).show();
                return false;
            }

//            if (time2IsAfterTime1(startSleep.getText().toString(), endSleep.getText().toString()) == false) {
//                Toast.makeText(UserInfo.this, "End sleep time cannot be earlier than start time.", Toast.LENGTH_SHORT).show();
//                return false;
//            }

            if (time2IsAfterTime1(endDinner.getText().toString(), startDinner.getText().toString()) == false) {
                Toast.makeText(UserInfo.this, "End dinner time cannot be earlier than start time.", Toast.LENGTH_SHORT).show();
                return false;
            }


            if (time2IsAfterTime1(endWkend.getText().toString(), startWkend.getText().toString()) == false) {
                Toast.makeText(UserInfo.this, "End weekend scheduling time cannot be earlier than start time.", Toast.LENGTH_SHORT).show();
                return false;
            }

            /* save this as activity and show calendar */
            //Toast.makeText(UserInfo.this, "Click Continue to move on.", Toast.LENGTH_LONG).show();

            return true;
        }

}

