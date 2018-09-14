package com.example.ashleighwilson.schoolscheduler.timetable;

import java.util.ArrayList;
import java.util.Calendar;

public class EventListHandler
{
    private static EventList eventList = null;

    private static ArrayList<CalendarEvent> events = null;
    private static int startTimeOfDay;
    private static int endTimeOfDay;

    public static ArrayList<CalendarEvent> getEvents() {
        return events;
    }

    public static void setEvents(ArrayList<CalendarEvent> events) {
        EventListHandler.events = events;
    }

    public static int getStartTimeOfDay() {
        return startTimeOfDay;
    }

    public static void setStartTimeOfDay(int startTimeOfDay) {
        EventListHandler.startTimeOfDay = startTimeOfDay;
    }

    public static int getEndTimeOfDay() {
        return endTimeOfDay;
    }

    public static void setEndTimeOfDay(int endTimeOfDay) {
        EventListHandler.endTimeOfDay = endTimeOfDay;
    }

    public static CalendarEvent getEventById(long id)
    {
        ArrayList<EventEvent> eventArrayList = eventList.getList();
        if (eventArrayList != null)
        {
            for (int i = 0; i < eventArrayList.size(); i++)
            {
                if (eventArrayList.get(i).getmId() == id)
                {
                    return eventArrayList.get(i);
                }
            }
        }
        return null;
    }

    public static void initEventList()
    {
        if (eventList == null)
            eventList = new EventList();
    }

    public static EventList getEventList()
    {
        return eventList;
    }

    public static void setEventList(EventList eventList)
    {
        EventListHandler.eventList = eventList;
    }

    public static void clearAllLists()
    {
        EventListHandler.eventList.setList(new ArrayList<EventEvent>());
        EventListHandler.eventList = null;
    }

    public static boolean checkValidTime(Calendar startTime, Calendar endTime)
    {
        if (startTime.compareTo(endTime) >= 0)
        {
            return false;
        }
        return true;
    }

    public static void createEventEvent(String name, String location, Calendar startTime,
                           Calendar endTime, int color, boolean isEvent)
    {
        /*boolean check = false;
        if (!checkValidTime(startTime, endTime))
        {
            return false;
        }
        if (startTime.get(Calendar.DAY_OF_MONTH) != endTime.get(Calendar.DAY_OF_MONTH))
        {
            return false;
        } */

        startTime.set(Calendar.SECOND, 0);
        endTime.set(Calendar.SECOND, 0);

        EventEvent eventEvent = new EventEvent(name, location, startTime, endTime, color, isEvent);
        eventEvent.setmId(System.currentTimeMillis());
        eventList.addEvent(eventEvent);

        //return (check);
    }

    public static boolean removeEventById(long temp)
    {
        boolean check = true;
        if (eventList == null)
        {
            check = false;
        }
        if (check == false)
        {
            return false;
        }
        check = eventList.removeEventById(temp);

        return check;
    }
}
