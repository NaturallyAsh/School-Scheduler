package com.example.ashleighwilson.schoolscheduler.timetable;

import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class WeekViewUtil {

    public static long eventId = 0;

    private static final String TAG = WeekViewUtil.class.getSimpleName();

    public static HashMap<String, WeekViewEvent> masterEvents = new HashMap();
    public static HashMap<String, List<WeekViewEvent>> monthMasterEvents = new HashMap();

    /////////////////////////////////////////////////////////////////
    //
    //      Helper methods.
    //
    /////////////////////////////////////////////////////////////////

    /**
     * Checks if two times are on the same day.
     * @param dayOne The first day.
     * @param dayTwo The second day.
     * @return Whether the times are on the same day.
     */
    public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
        return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
    }
    /*
    private static Calendar sDayOne = Calendar.getInstance();
    private static Calendar sDayTwo = Calendar.getInstance();
    public static boolean isSameDay(long dayOne, long dayTwo) {

    }*/

    /**
     * Returns a calendar instance at the start of this day
     * @return the calendar instance
     */
    public static Calendar today(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today;
    }

    public static void sortAndCacheEvents(List<WeekViewEvent> events, List<EventRect> mEventRects)
    {
        sortEvents(events);
        for (WeekViewEvent event: events)
        {
            cacheEvent(event, mEventRects);
        }
    }

    private static void sortEvents(List<WeekViewEvent> events)
    {
        Collections.sort(events, (event1, event2) -> {
            long start1 = event1.getStartTime().getTimeInMillis();
            long start2 = event2.getStartTime().getTimeInMillis();
            int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
            if (comparator == 0) {
                long end1 = event1.getEndTime().getTimeInMillis();
                long end2 = event2.getEndTime().getTimeInMillis();
                comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);

            }
            return comparator;
        });
    }

    private static void cacheEvent(WeekViewEvent event, List<EventRect> mEventRects)
    {
        /*if (event.getStartTime().compareTo(event.getEndTime()) >= 0) {
            Log.i(TAG, "returned");

            return;
        }*/
        List<WeekViewEvent> splitedEvents = event.splitWeekViewEvents();
        for (WeekViewEvent splitedEvent: splitedEvents)
        {
            mEventRects.add(new EventRect(splitedEvent, event, null));
        }
    }

    public static boolean isSameDayAndHour(Calendar dateOne, Calendar dateTwo) {

        if (dateTwo != null) {
            return isSameDay(dateOne, dateTwo) && dateOne.get(Calendar.HOUR_OF_DAY) == dateTwo.get(Calendar.HOUR_OF_DAY);
        }
        return false;
    }

    /**
     * Returns the amount of days between the second date and the first date
     *
     * @param dateOne the first date
     * @param dateTwo the second date
     * @return the amount of days between dateTwo and dateOne
     */
    public static int daysBetween(Calendar dateOne, Calendar dateTwo) {
        return (int) (((dateTwo.getTimeInMillis() + dateTwo.getTimeZone().getOffset(dateTwo.getTimeInMillis())) / (1000 * 60 * 60 * 24)) -
                ((dateOne.getTimeInMillis() + dateOne.getTimeZone().getOffset(dateOne.getTimeInMillis())) / (1000 * 60 * 60 * 24)));
    }

    /*
     * Returns the amount of minutes passed in the day before the time in the given date
     * @param date
     * @return amount of minutes in day before time
     */
    public static int getPassedMinutesInDay(Calendar date) {
        return getPassedMinutesInDay(date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
    }

    /**
     * Returns the amount of minutes in the given hours and minutes
     *
     * @param hour
     * @param minute
     * @return amount of minutes in the given hours and minutes
     */
    public static int getPassedMinutesInDay(int hour, int minute) {
        return hour * 60 + minute;
    }
}
