package com.example.ashleighwilson.schoolscheduler.notes;

import android.support.v4.app.FragmentActivity;

import com.example.ashleighwilson.schoolscheduler.dialog.SimpleDateDialog;
import com.example.ashleighwilson.schoolscheduler.dialog.SimpleTimeDialog;

public class ReminderPickers
{
    private static final String TAG = ReminderPickers.class.getSimpleName();

    private FragmentActivity fragmentActivity;
    private OnReminderPickedListener mOnReminderPickedListener;
    private static final String DATE_DIALOG = "date dialog";
    private static final String TIME_DIALOG = "time dialog";

    private int reminderYear;
    private int reminderMonth;
    private int reminderDay;
    private int hourOfDay;
    private int minutes;
    private String recurrenceRule;

    public ReminderPickers(FragmentActivity activity, OnReminderPickedListener reminderPickedListener)
    {
        this.fragmentActivity = activity;
        this.mOnReminderPickedListener = reminderPickedListener;
    }


}
