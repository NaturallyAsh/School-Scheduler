package com.example.ashleighwilson.schoolscheduler.views;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appeaser.sublimepickerlibrary.SublimePicker;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeListenerAdapter;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.example.ashleighwilson.schoolscheduler.NotesActivity;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.notes.OnReminderPickedListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RecurrenceDialog extends DialogFragment
{
    private static final String TAG = RecurrenceDialog.class.getSimpleName();

    DateFormat mDateFormatter, mTimeFormatter;
    SublimePicker mSublimePicker;
    Callback mCallback;
    NotesActivity notesActivity;
    OnReminderPickedListener mOnReminderPickedListener;
    SelectedDate mSelectedDate;
    int mHour, mMinute, mReminderYear, mReminderMonth, mReminderDay;
    String mRecurrenceOption, mRecurrenceRule;


    SublimeListenerAdapter mListener = new SublimeListenerAdapter() {
        @Override
        public void onDateTimeRecurrenceSet(SublimePicker sublimeMaterialPicker, SelectedDate selectedDate,
                                            int hourOfDay, int minute, SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {
            mSelectedDate = selectedDate;
            mReminderYear = mSelectedDate.getFirstDate().get(Calendar.YEAR);
            mReminderMonth = mSelectedDate.getFirstDate().get(Calendar.MONTH);
            mReminderDay = mSelectedDate.getFirstDate().get(Calendar.DAY_OF_MONTH);

            //Log.i(TAG, "year: " + mReminderYear + " month: " + mReminderMonth + " day: " + mReminderDay);
            mHour = hourOfDay;
            mMinute = minute;
            mRecurrenceOption = recurrenceOption != null ? recurrenceOption.name() : "n/a";
            mRecurrenceRule = recurrenceRule != null ? recurrenceRule : "n/a";

            //Log.i(TAG, "selected date: " + selectedDate + " hour: " + hourOfDay + " minute: " +
              //      minute + " recurrence rule: " + recurrenceRule);

            Calendar calendar = Calendar.getInstance();
            calendar.set(mReminderYear, mReminderMonth, mReminderDay, mHour, mMinute, 0);

            if (mCallback != null)
            {
                mCallback.onDateTimeRecurrenceSet(selectedDate, hourOfDay, minute, recurrenceOption, recurrenceRule);

                //mOnReminderPickedListener.onReminderPicked(calendar.getTimeInMillis());
                //mOnReminderPickedListener.onRecurrenceReminderPicked(mRecurrenceRule);
                //mCallback.onSetCallbackReminder(mOnReminderPickedListener);

            }
            dismiss();
        }

        @Override
        public void onCancelled() {
            if (mCallback != null)
            {
                mCallback.onCancelled();
            }
            dismiss();
        }
    };

    public interface Callback {
        void onCancelled();
        void onDateTimeRecurrenceSet(SelectedDate selectedDate,
                                     int hourOfDay, int minute,
                                     SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                     String recurrenceRule);
    }

    public RecurrenceDialog()
    {
        mDateFormatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        mTimeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
        //mTimeFormatter.setTimeZone(T);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mSublimePicker = (SublimePicker) inflater.inflate(R.layout.sublime_picker, container);

        Bundle args = getArguments();
        SublimeOptions options = null;

        if (args != null)
        {
            options = args.getParcelable("SUBLIME_OPTIONS");
        }

        mSublimePicker.initializePicker(options, mListener);
        return mSublimePicker;
    }

    private NotesActivity getNotesActivity() {
        return (NotesActivity) getActivity();
    }
}
