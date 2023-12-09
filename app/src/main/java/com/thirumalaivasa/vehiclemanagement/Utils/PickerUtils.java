package com.thirumalaivasa.vehiclemanagement.Utils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;

import java.util.Calendar;

public class PickerUtils {

    public static void showDatePicker(Context context, OnDateSelectedListener listener) {
        // Use the current date as the default
        showDatePicker(context, listener, Calendar.getInstance());
    }

    public static void showDatePicker(Context context, OnDateSelectedListener listener, Calendar initialDate) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context, (view, year, month, dayOfMonth) -> {
            // Callback to the listener with selected date
            if (listener != null) {
                listener.onDateSelected(year, month, dayOfMonth);
            }
        },
                initialDate.get(Calendar.YEAR),
                initialDate.get(Calendar.MONTH),
                initialDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    public static void showTimePicker(Context context, OnTimeSelectedListener listener) {
        showTimePicker(context, listener, Calendar.getInstance(), false);
    }

    public static void showTimePicker(Context context, OnTimeSelectedListener listener, Calendar initialDate) {
        showTimePicker(context, listener, initialDate, false);
    }

    public static void showTimePicker(Context context, OnTimeSelectedListener listener, Calendar initialTime, boolean format) {
        int initialHour = initialTime.get(Calendar.HOUR_OF_DAY);
        int initialMinute = initialTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                (view, hourOfDay, minute) -> {
                    // Callback to the listener with selected time
                    if (listener != null) {
                        listener.onTimeSelected(hourOfDay, minute);
                    }
                },
                initialHour,
                initialMinute,
                format // set to true for 24-hour format, false for 12-hour format
        );

        timePickerDialog.show();
    }

    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day);
    }

    public interface OnTimeSelectedListener {
        void onTimeSelected(int hour, int min);
    }
}
