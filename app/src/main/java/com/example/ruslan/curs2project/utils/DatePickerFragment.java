package com.example.ruslan.curs2project.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Ruslan on 16.02.2018.
 */

public class DatePickerFragment extends DialogFragment {

    private OnDateListener releaseListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

    public OnDateListener getReleaseListener() {
        return releaseListener;
    }

    public void setReleaseListener(OnDateListener releaseListener) {
        this.releaseListener = releaseListener;
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {

                    GregorianCalendar calendar = new GregorianCalendar(year,month,day);
                    releaseListener.onDateChanged(calendar);


                    Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                            " / " + (view.getMonth() + 1) +
                            " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
                }
            };

}
