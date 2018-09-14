package com.example.ashleighwilson.schoolscheduler.timetable;

import java.util.Calendar;

public interface CalendarEvent extends CalendarObject
{
    long getmId();
    void setmId(long id);
    boolean isEvent();
    void setEvent(boolean isEvent);
    String getmName();
    void setmName(String name);
    Calendar getmStartTime();
    void setmStartTime(Calendar startTime);
    Calendar getmEndTime();
    void setmEndTime(Calendar endTime);
    int getmColor();
    void setmColor(int color);
    String getmLocation();
    void setmLocation(String location);
}
