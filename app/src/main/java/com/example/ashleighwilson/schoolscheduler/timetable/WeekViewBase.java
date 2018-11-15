package com.example.ashleighwilson.schoolscheduler.timetable;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.MySchedulerApp;
import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.WeekViewFragment;
import com.example.ashleighwilson.schoolscheduler.data.DbHelper;

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
    WeekViewEvent loadedEvent;

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
        if (eventListByMonth == null)
        {
            eventListByMonth = new ArrayList<>();
        }
        else
        {
            eventListByMonth.clear();
        }

        List<WeekViewEvent> events = new ArrayList<>();

        /*Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 3);
        endTime.set(Calendar.MONTH, newMonth - 1);
        WeekViewEvent event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime),
                startTime, endTime);
        event.setColor(getResources().getColor(R.color.AliceBlue));
        event.setLocation(event.getLocation());
        events.add(event);

        WeekViewUtil.masterEvents.put("" + event.getId(), event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 8);
        startTime.set(Calendar.MINUTE, 30);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 9);
        endTime.set(Calendar.MINUTE, 30);
        endTime.set(Calendar.MONTH, newMonth-1);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime),
                startTime, endTime);
        event.setColor(getResources().getColor(R.color.Green));
        event.setLocation(event.getLocation());
        events.add(event);

        WeekViewUtil.masterEvents.put("" + event.getId(), event); */

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

            WeekViewEvent dbEvent = new WeekViewEvent(id, getEventName(name, start, end), start, end);
            dbEvent.setColor(color);
            dbEvent.setLocation(location);

            events.add(dbEvent);
            //WeekViewFragment.notifyWeekView();

            WeekViewUtil.masterEvents.put("" + dbEvent.getId(), dbEvent);

        }

        //WeekViewEvent fileEvent = CalenderFrag.readObj(mContext);
        //events.add(fileEvent);

        eventListByMonth.addAll(events);
        WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);


        //listener.refreshData(events);
        return events;
    }
}
