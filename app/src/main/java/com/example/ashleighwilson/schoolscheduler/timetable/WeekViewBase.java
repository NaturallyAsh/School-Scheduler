package com.example.ashleighwilson.schoolscheduler.timetable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.R;
import com.example.ashleighwilson.schoolscheduler.WeekViewFragment;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class WeekViewBase extends WeekViewFragment
{
    //private static final String TAG = WeekViewBase.class.getSimpleName();
    List<WeekViewEvent> eventListByMonth;
    String monthKey;
    private static long eventViewId;
    private static final String ARG_MONTH_KEY = "arg_month_key";
    private static final String ARG_EVENT_LIST = "arg_event_list";
    public final static String ARG_EVENT_ID = "arg_event_id";

    public static WeekViewBase newInstance(long event_id)
    {
        WeekViewBase fragment = new WeekViewBase();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_EVENT_ID, event_id);
        //bundle.putSerializable("events", (Serializable) eventInstance);
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
        //eventListByMonth = (List<WeekViewEvent>) getArguments().getSerializable("events");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            eventViewId = bundle.getLong(ARG_EVENT_ID);
        }
    }

    @Override
    public List<WeekViewEvent> onMonthLoad(int newYear, int newMonth) {

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

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR, 1);
        endTime.set(Calendar.MONTH, newMonth - 1);
        WeekViewEvent event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime),
                startTime, endTime);
        event.setColor(getResources().getColor(R.color.AliceBlue));
        event.setLocation(event.getLocation());
        events.add(event);

        WeekViewUtil.masterEvents.put("" + event.getId(), event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 30);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.HOUR_OF_DAY, 4);
        endTime.set(Calendar.MINUTE, 30);
        endTime.set(Calendar.MONTH, newMonth-1);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime),
                startTime, endTime);
        event.setColor(getResources().getColor(R.color.Green));
        event.setLocation(event.getLocation());
        events.add(event);

        WeekViewUtil.masterEvents.put("" + event.getId(), event);

        /*startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 5);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        startTime.add(Calendar.DATE, 1);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        endTime.set(Calendar.MONTH, newMonth - 1);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_03));
        events.add(event);

        WeekViewUtil.masterEvents.put(""+event.getId(), event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 15);
        startTime.set(Calendar.HOUR_OF_DAY, 3);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 3);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_03));
        events.add(event);

        WeekViewUtil.masterEvents.put(""+event.getId(), event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 15);
        startTime.set(Calendar.HOUR_OF_DAY, 1);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 1);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_04));
        events.add(event);

        WeekViewUtil.masterEvents.put(""+event.getId(), event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 15);
        startTime.set(Calendar.HOUR_OF_DAY, 18);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 2);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime), startTime, endTime);
        event.setColor(getResources().getColor(R.color.event_color_01));
        events.add(event);

        WeekViewUtil.masterEvents.put(""+event.getId(), event);

        //AllDay event
        startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 23);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime),null, startTime, endTime, true);
        event.setColor(getResources().getColor(R.color.event_color_04));
        events.add(event);

        WeekViewUtil.masterEvents.put(""+event.getId(), event);

        // All day event until 00:00 next day
        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 10);
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.SECOND, 0);
        startTime.set(Calendar.MILLISECOND, 0);
        startTime.set(Calendar.MONTH, newMonth-1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.DAY_OF_MONTH, 11);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime), null, startTime, endTime, true);
        event.setColor(getResources().getColor(R.color.event_color_02));
        events.add(event);

        WeekViewUtil.masterEvents.put(""+event.getId(), event);

        startTime = Calendar.getInstance();
        startTime.set(Calendar.DAY_OF_MONTH, 27);
        startTime.set(Calendar.HOUR_OF_DAY, 10);
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.MONTH, newMonth - 1);
        startTime.set(Calendar.YEAR, newYear);
        endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 1);
        endTime.set(Calendar.MONTH, newMonth - 1);
        endTime.set(Calendar.DAY_OF_MONTH, 28);
        event = new WeekViewEvent(WeekViewUtil.eventId++, getEventTitle(startTime, endTime), null, startTime, endTime, true);
        event.setColor(getResources().getColor(R.color.event_color_01));
        events.add(event);

        WeekViewUtil.masterEvents.put(""+event.getId(), event); */

        eventListByMonth.addAll(events);
        WeekViewUtil.monthMasterEvents.put(monthKey, eventListByMonth);

        listener.refreshData(events);
        //listener.refreshData(eventListByMonth);
        return events;
    }

    public List<WeekViewEvent> loadEvents(List<WeekViewEvent> events)
    {
        if (eventListByMonth != null)
            return eventListByMonth;
        else
            return null;
    }
}
