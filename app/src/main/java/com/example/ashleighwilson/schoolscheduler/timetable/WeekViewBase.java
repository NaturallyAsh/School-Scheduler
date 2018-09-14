package com.example.ashleighwilson.schoolscheduler.timetable;

import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.WeekViewFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeekViewBase extends WeekViewFragment
{
    private static final String TAG = WeekViewBase.class.getSimpleName();

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        ArrayList<? extends CalendarEvent> eventList = new ArrayList<>();

        try
        {
            eventList = EventListHandler.getEventList().getList();
            Log.d(TAG, "event length " + eventList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < eventList.size(); i++)
        {
            EventEvent event_temp = (EventEvent) eventList.get(i);

            DateFormat time = new SimpleDateFormat("MM");
            int event_month = Integer.parseInt(time.format(event_temp.getmStartTime().getTime()));

            if (event_month == newMonth)
            {
                String description = "";
                description += event_temp.getmName();
                if (!event_temp.getmLocation().equals(""))
                {
                    description += " - " + event_temp.getmLocation();
                }

                Calendar startTime = (Calendar)event_temp.getmStartTime().clone();
                startTime.add(Calendar.MINUTE,1);
                Calendar endTime = (Calendar)event_temp.getmEndTime().clone();
                endTime.add(Calendar.MINUTE, -1);

                WeekViewEvent event = new WeekViewEvent(event_temp.getmId(), description,
                        startTime, endTime);
                event.setColor(event_temp.getmColor());
                events.add(event);
            }
        }


        return events;
    }
}
