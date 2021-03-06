package com.example.technovation2021;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import androidx.fragment.app.DialogFragment;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public int idTextView;

    public DatePickerFragment(int idEventStartDate) {
        idTextView = idEventStartDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        //TextView tv = (TextView) getActivity().findViewById(R.id.idEventStartDate);
        TextView tv = (TextView) getActivity().findViewById(idTextView);
        tv.setText(String.format("%02d",month + 1) + "/" + String.format("%02d", day)+"/"+String.valueOf(year));
    }
}
