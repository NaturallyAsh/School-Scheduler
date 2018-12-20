package com.example.ashleighwilson.schoolscheduler.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrenceFormatter;
import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.notes.Constants;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.lang.StringUtils;
import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper{
    private static final String TAG = DateHelper.class.getSimpleName();

    private static final SimpleDateFormat SHORT_WEEK_DAYS_FORMAT = new SimpleDateFormat("E", Locale.getDefault());
    private static final SimpleDateFormat WEEK_DAYS_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());

    public static String dateFormatter(Long time)
    {
        if (time == null)
            return "";

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy, hh:mm a");
        String msg = sdf.format(calendar.getTime());

        return msg;
    }

    public static String formatRecurrence(Context context, String recurrenceRule)
    {
        if (!TextUtils.isEmpty(recurrenceRule))
        {
            EventRecurrence eventRecurrence = new EventRecurrence();
            eventRecurrence.setStartDate(new Time("" + new Date().getTime()));
            //eventRecurrence.parse(recurrenceRule.replace("WKST=SU", "WKST=MO"));
            eventRecurrence.parse(recurrenceRule);
            return EventRecurrenceFormatter.getRepeatString(context.getApplicationContext(),
                    context.getResources(), eventRecurrence, true);
        } else {
            return "";
        }
    }

    public static String getNoteRecurrentReminderText(long reminder, String rule)
    {
        return formatRecurrence(MySchedulerApp.getInstance(), rule) + " " + "Starting from " +
                dateFormatter(reminder);
    }

    public static String getNoteReminderText(String msg, long reminder)
    {
        return msg + getDateTimeShort(MySchedulerApp.getInstance(), reminder);
    }

    public static boolean isFuture(long timestamp)
    {
        try {
            return timestamp > Calendar.getInstance().getTimeInMillis();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFuture(String timestamp) {
        return !StringUtils.isEmpty(timestamp) && isFuture(Long.parseLong(timestamp));
    }

    public static Long nextReminderFromRecurrenceRule(long reminder, String recurrenceRule)
    {
        return nextReminderFromRecurrenceRule(reminder, Calendar.getInstance().getTimeInMillis(),
                recurrenceRule);
    }

    public static Long nextReminderFromRecurrenceRule(long reminder, long currentTime, String recurrenceRule)
    {
        RRule rule = new RRule();
        try {
            rule.setValue(recurrenceRule);
            DateTime seed = new DateTime(reminder);
            //DateTime seed = new DateTime(currentTime);
            //long startTimestamp = reminder + 60 * 1000;
            long startTimestamp = reminder + 60 * 1000;
            if (startTimestamp < currentTime) {
                startTimestamp = currentTime;
            }

            DateTime start = new DateTime(startTimestamp);
            Date nextDate = rule.getRecur().getNextDate(seed, start);
            return nextDate == null ? 0L : nextDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static String formatShortTime(Context mContext, long time) {
        String m = String.valueOf(time / 1000 / 60);
        String s = String.format("%02d", (time / 1000) % 60);
        return m + ":" + s;
    }

    public static String getLocalizedDateTime(Context mContext,
                                              String dateString, String format) {
        String res = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            sdf = new SimpleDateFormat(Constants.DATE_FORMAT_SORTABLE_OLD);
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e1) {
                Log.e(TAG, "String is not formattable into date");
            }
        }

        if (date != null) {
            String dateFormatted = android.text.format.DateUtils.formatDateTime(mContext, date.getTime(), android
                    .text.format.DateUtils.FORMAT_ABBREV_MONTH);
            String timeFormatted = android.text.format.DateUtils.formatDateTime(mContext, date.getTime(), android
                    .text.format.DateUtils.FORMAT_SHOW_TIME);
            res = dateFormatted + " " + timeFormatted;
        }

        return res;
    }

    public static String getDateTimeShort(Context mContext, Long date) {
        int flags = DateUtils.FORMAT_ABBREV_WEEKDAY | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE;

        return (date == null) ? "" : DateUtils.formatDateTime(mContext, date, flags)
                + " " + DateUtils.formatDateTime(mContext, date, DateUtils.FORMAT_SHOW_TIME);
    }

    public static String prettyTime(Long timeInMilli) {
        return prettyTime(timeInMilli, MySchedulerApp.getInstance().getResources().getConfiguration().locale);
    }

    public static String prettyTime(Long timeInmilli, Locale locale) {
        if (timeInmilli == null)
            return "";

        Date date = new Date(timeInmilli);
        PrettyTime pt = new PrettyTime();
        if (locale != null) {
            pt.setLocale(locale);
        }

        return pt.format(date);
    }

    public static String[] getShortWeekDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String[] weekDays = new String[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = SHORT_WEEK_DAYS_FORMAT.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return weekDays;
    }

    public static String[] getWeekDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String[] weekDays = new String[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = WEEK_DAYS_FORMAT.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return weekDays;
    }
}
