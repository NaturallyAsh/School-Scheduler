package com.example.ashleighwilson.schoolscheduler.timetable;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.appeaser.sublimepickerlibrary.recurrencepicker.EventRecurrence;
import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.WeekViewFragment;
import com.example.ashleighwilson.schoolscheduler.adapter.EventAdapter;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;
import com.example.ashleighwilson.schoolscheduler.models.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekViewBase extends WeekViewFragment
{
    private static final String TAG = WeekViewBase.class.getSimpleName();
    List<WeekViewEvent> eventListByMonth;
    String monthKey;
    private static long eventViewId;
    public final static String ARG_EVENT_ID = "arg_event_id";
    Context mContext = MySchedulerApp.getInstance();
    DbHelper dbHelper = DbHelper.getInstance();
    WeekViewEvent dbEvent;
    List<WeekViewEvent> deleteEvents = new ArrayList<>();

    public static WeekViewBase newInstance(long event_id)
    {
        WeekViewBase fragment = new WeekViewBase();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_EVENT_ID, event_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    OnFragmentInteractionListener listener;

    public void onAttachToPF(Fragment fragment)
    {
        try {
            listener = (OnFragmentInteractionListener) fragment;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onAttachToPF(getParentFragment());
        setRetainInstance(true);
        Log.i(TAG, "onCreate!");
        mContext = MySchedulerApp.getInstance();
        deleteEvents = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            eventViewId = bundle.getLong(ARG_EVENT_ID);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(TAG, "onResume!");
    }

    @Override
    public List<WeekViewEvent> onMonthLoad(int newYear, int newMonth) {
        Log.i(TAG, "onMonthLoad");

        monthKey = "" + (newMonth - 1) + "-" + newYear;
        eventListByMonth = WeekViewUtil.monthMasterEvents.get(monthKey);
        //Log.i(TAG, "event by month size: " + eventListByMonth.size());
        if (eventListByMonth == null)
        {
            eventListByMonth = new ArrayList<>();
        }
        else
        {
            eventListByMonth.clear();
        }

        List<WeekViewEvent> events = new ArrayList<>();

        Cursor cursor = dbHelper.fetchEvents();

        while (cursor.moveToNext())
        {
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            String location = cursor.getString(2);
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(cursor.getLong(3));
            start.set(Calendar.HOUR_OF_DAY, start.get(Calendar.HOUR_OF_DAY));
            start.set(Calendar.MINUTE, start.get(Calendar.MINUTE));
            start.set(Calendar.MONTH, newMonth -1);
            start.set(Calendar.YEAR, newYear);
            Calendar end = Calendar.getInstance();
            //end = (Calendar) start.clone();
            end.setTimeInMillis(cursor.getLong(4));
            end.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY));
            end.set(Calendar.MINUTE, end.get(Calendar.MINUTE));
            end.set(Calendar.MONTH, newMonth -1);
            int color = cursor.getInt(5);
            String rule = cursor.getString(6);
            //String dayOfWeek = cursor.getString(7);

            /*Log.i(TAG, "rule: " + rule);

            int day_recur = 0;
            if (rule != null) {
                EventRecurrence recurrence = new EventRecurrence();
                recurrence.parse(rule);
                for (int i = 0; i < recurrence.bydayCount; i++) {
                    day_recur = EventRecurrence.day2CalendarDay(recurrence.byday[i]);
                    Log.i(TAG, "day recur: " + day_recur);
                }
                Calendar c = Calendar.getInstance();
                recurrence.wkst = c.get(Calendar.DAY_OF_WEEK);
                while (recurrence.wkst != day_recur) {
                    c.add(Calendar.DAY_OF_WEEK, day_recur);
                    recurrence.wkst = c.get(Calendar.DAY_OF_WEEK);
                    Log.i(TAG, "recurrence wkst: " + recurrence.wkst);
                }
            }*/

            dbEvent = new WeekViewEvent(id, name, start, end);
            dbEvent.setColor(color);
            dbEvent.setLocation(location);
            dbEvent.setmRecurrenceRule(rule);

            events.add(dbEvent);
            WeekViewUtil.masterEvents.put("" + dbEvent.getId(), dbEvent);
        }

        //WeekViewEvent fileEvent = CalenderFrag.readObj(mContext);
        //events.add(fileEvent);
        eventListByMonth.addAll(events);
        WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);

        deleteEvents.addAll(events);
        addLoadedEvents(events);
        return events;
    }

    public void removeEvent(WeekViewEvent event) {
        {
            int month = event.getStartTime().get(Calendar.MONTH - 1);
            int year = event.getStartTime().get(Calendar.YEAR);
            deleteEvents = onMonthLoad(year, month);
            Log.i(TAG, "deleted event: " + deleteEvents.size());
            if (deleteEvents == null) {
                deleteEvents = new ArrayList<>();
                Log.i(TAG, "events null");
            }
            if (deleteEvents.size() > 1) {
                Log.i(TAG, "events greater than 1");
                for (WeekViewEvent e : deleteEvents) {
                    if (e.getStartTime().getTime() == event.getStartTime().getTime()) {
                        Log.i(TAG, "e start: " + e.getStartTime() + " event start: " + event.getStartTime());
                        deleteEvents.remove(e);
                        notifyWeekView();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
