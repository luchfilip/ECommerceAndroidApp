package com.smartbuilders.smartsales.ecommerce.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by stein on 23/6/2016.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Date date;

    public interface Callback{
        void setDate(String date);
    }

    public static DatePickerFragment getInstance(Date date){
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.date = date;
        return dpf;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        if(date!=null){
            c.setTime(date);
        }
        return new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        ((Callback) getTargetFragment()).setDate(DateFormat.getDateInstance(DateFormat.MEDIUM,
                new Locale("es","VE")).format((new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTime()));
    }
}
