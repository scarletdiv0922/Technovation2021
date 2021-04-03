package com.example.technovation2021;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class UserInfoAddActivity extends AppCompatActivity {

    private static final String LOG_TAG = AddActivity.class.getSimpleName();
    EditText activityDes;
    Button sunday;
    Button monday;
    Button tuesday;
    Button wednesday;
    Button thursday;
    Button friday, saturday;
    Button addActivity, cancelActivity;
    TextView startTime;
    TextView endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_add);

        activityDes = findViewById(R.id.activityDescription);
        sunday = findViewById(R.id.btnSunday);
        monday = findViewById((R.id.btnMonday));
        tuesday = findViewById((R.id.btnTuesday));
        wednesday = findViewById(R.id.btnWednesday);
        thursday = findViewById(R.id.btnThursday);
        friday = findViewById(R.id.btnFriday);
        saturday = findViewById(R.id.btnSaturday);
        startTime = findViewById(R.id.txtUserInfoStartTime);
        endTime = findViewById(R.id.txtUserInfoEndTime);


        sunday.setBackgroundColor(Color.WHITE);
        monday.setBackgroundColor(Color.WHITE);
        tuesday.setBackgroundColor(Color.WHITE);
        wednesday.setBackgroundColor(Color.WHITE);
        thursday.setBackgroundColor(Color.WHITE);
        friday.setBackgroundColor(Color.WHITE);
        saturday.setBackgroundColor(Color.WHITE);

//        EditText activity_desc = findViewById(R.id.activityDescription);
//        if (TextUtils.isEmpty(activity_desc.getText().toString()) ) {
//            activity_desc.setError("Activity name cannot be empty.");
//            return;
//        }

        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x1 = (ColorDrawable) sunday.getBackground();
                if ( x1.getColor() == Color.WHITE ) {
                    sunday.setBackgroundColor( Color.BLUE);
                }
                else {
                    sunday.setBackgroundColor( Color.WHITE);
                }
            }
        });
        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x2 = (ColorDrawable) monday.getBackground();
                if ( x2.getColor() == Color.WHITE ) {
                    monday.setBackgroundColor( Color.BLUE);
                }
                else {
                    monday.setBackgroundColor( Color.WHITE);
                }
            }
        });
        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x3 = (ColorDrawable) tuesday.getBackground();
                if ( x3.getColor() == Color.WHITE ) {
                    tuesday.setBackgroundColor( Color.BLUE);
                }
                else {
                    tuesday.setBackgroundColor( Color.WHITE);
                }
            }
        });
        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x4 = (ColorDrawable) wednesday.getBackground();
                if ( x4.getColor() == Color.WHITE ) {
                    wednesday.setBackgroundColor( Color.BLUE);
                }
                else {
                    wednesday.setBackgroundColor( Color.WHITE);
                }
            }
        });
        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x5 = (ColorDrawable) thursday.getBackground();
                if ( x5.getColor() == Color.WHITE ) {
                    thursday.setBackgroundColor( Color.BLUE);
                }
                else {
                    thursday.setBackgroundColor( Color.WHITE);
                }
            }
        });
        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorDrawable x6 = (ColorDrawable) friday.getBackground();
                if ( x6.getColor() == Color.WHITE ) {
                    friday.setBackgroundColor( Color.BLUE);
                }
                else {
                    friday.setBackgroundColor( Color.WHITE);
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
                    saturday.setBackgroundColor(Color.BLUE);
                }
                else {
                    saturday.setBackgroundColor( Color.WHITE);
                }
            }
        });
    }

    public void userInfoStartTimeClicked(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.txtUserInfoStartTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "start time clicked");
    }

    public void txtUserInfoEndTime(View view) {
        DialogFragment newFragment = new TimePickerFragment(R.id.txtUserInfoEndTime);
        newFragment.show(getSupportFragmentManager(), "timePicker");
        Log.d(LOG_TAG, "end time clicked");
    }






//    private boolean noErrorsOnScreen() {
//        if ( noDaysSelected() )
//            return false;
//
//        if ( !startAndEndTimesAreValid() )
//            return false;
//
//        if ( activityDescriptionIsEmpty() )
//            return false;
//
//        return true;
//    }
//
//    private boolean activityDescriptionIsEmpty() {
//
//    }
//
//    private boolean startAndEndTimesAreValid() {
//    }
//
//    private boolean noDaysSelected() {
//
//    }
//
//    public void userInfoActivityAdd(View view) {
//        if ( noErrorsOnScreen() ) {
//            /* Its all good */
//        }
//    }
}