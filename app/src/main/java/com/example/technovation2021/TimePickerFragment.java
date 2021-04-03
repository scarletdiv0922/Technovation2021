package com.example.technovation2021;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    public TimePickerFragment(int endDinnerTime) {

    }


    int resId;

//    public TimePickerFragment(int _resId) {
//        super();
//        resId = _resId;
//    }

    int resourceId;

//    public TimePickerFragment(int resId) {
//        resourceId = resId;
//    }
//
//    public TimePickerFragment(int idStartTime)
//    }
//
////    public TimePickerFragment(int idStartTime) {
////    }


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        Log.d("Time picker", "time picked " + hourOfDay + " " + minute);
        //TextView tv = (TextView) getActivity().findViewById(R.id.idStartTime);
        TextView tv = (TextView) getActivity().findViewById(resId);
//        TextView tv = (TextView) getActivity().findViewById(resourceId);
        String time = "";
        if ( hourOfDay > 12 )
            time = String.valueOf(hourOfDay - 12);
        else time = String.valueOf(hourOfDay);
        time += ":";
        if ( minute <= 9 ) time += "0";
        time += String.valueOf(minute);
        if ( hourOfDay > 12 )
            time += " PM";
        else time += " AM";
        tv.setText(time);
    }
}
