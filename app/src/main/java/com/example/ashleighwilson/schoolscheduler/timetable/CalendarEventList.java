package com.example.ashleighwilson.schoolscheduler.timetable;

import java.util.ArrayList;

public interface CalendarEventList
{
    ArrayList<EventEvent> getList();
    void setList(ArrayList<EventEvent> list);
    void addEvent(EventEvent event);
}
