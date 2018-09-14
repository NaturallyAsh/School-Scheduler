package com.example.ashleighwilson.schoolscheduler.timetable;

import java.util.ArrayList;

public class EventList implements CalendarObjectList<ArrayList<EventEvent>, EventEvent> {


    private ArrayList<EventEvent> eventEvents;

    public EventList()
    {
        this.eventEvents = new ArrayList<EventEvent>();
    }

    public ArrayList<EventEvent> getList() {
        return eventEvents;
    }

    public void setList(ArrayList<EventEvent> list) {
        this.eventEvents = list;
    }

    public void addEvent(EventEvent event)
    {
        boolean check = true;
        if (event == null)
        {check = false;}

        this.eventEvents.add(event);
    }

    public boolean removeEventById(long temp)
    {
        boolean check = false;
        EventEvent eventToRemove;
        for (int i = 0; i< eventEvents.size(); i++)
        {
            eventToRemove = eventEvents.get(i);
            if (eventToRemove.getmId() == temp)
            {
                eventEvents.remove(eventToRemove);
                check = true;
            }
        }
        return check;
    }
}
