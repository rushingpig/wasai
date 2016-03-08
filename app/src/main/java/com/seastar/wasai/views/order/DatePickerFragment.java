package com.seastar.wasai.views.order;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import com.seastar.wasai.views.extendedcomponent.MyDatePickerDialog;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {
    private FindOrderActivity findOrderActivity;
    public DatePickerFragment(){
        super();
    }

    public void setActivity(FindOrderActivity findOrderActivity){
        this.findOrderActivity = findOrderActivity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Dialog dialog = new MyDatePickerDialog(getActivity(), this, year, month, day);
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        findOrderActivity.setDate(year + "-" + (month + 1) + "-" + day);
    }
}