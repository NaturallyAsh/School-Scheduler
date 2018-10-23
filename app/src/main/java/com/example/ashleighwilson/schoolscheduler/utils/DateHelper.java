package com.example.ashleighwilson.schoolscheduler.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Time;

import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrenceFormatter;
import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.RRule;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper
{

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

    public static String formateRecurrence(Context context, String recurrenceRule)
    {
        if (!TextUtils.isEmpty(recurrenceRule))
        {
            EventRecurrence eventRecurrence = new EventRecurrence();
            eventRecurrence.setStartDate(new Time("" + new Date().getTime()));
            eventRecurrence.parse(recurrenceRule);
            return EventRecurrenceFormatter.getRepeatString(context.getApplicationContext(),
                    context.getResources(), eventRecurrence, true);
        } else {
            return "";
        }
    }

    public static String getNoteRecurrentReminderText(long reminder, String rule)
    {
        return formateRecurrence(MySchedulerApp.getInstance(), rule) + " " + "Starting from " +
                dateFormatter(reminder);
    }

    public static String getNoteReminderText(String msg, long reminder)
    {
        return msg + dateFormatter(reminder);
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
}
