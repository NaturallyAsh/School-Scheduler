package com.example.ashleighwilson.schoolscheduler.timetable;

import android.util.Log;

import com.example.ashleighwilson.schoolscheduler.timetable.CalendarEvent;

import java.util.Calendar;

public class EventEvent implements CalendarEvent
{
    private static final String TAG = EventEvent.class.getSimpleName();


    private long Id; //Random Long Number
    //private String dateKey; //dateKey is the date of the event 12 Feb 2016
    private String name;  //name of the event

    private Calendar startTime; //startTime as a Calendar object
    private Calendar endTime; //endTime as a Calendar object
    private boolean isStatic; //if the event is static
    private boolean isPeriodic; //if the event is periodic
    private boolean isFinished; //if the event is finished
    private String location = ""; //location of event
    private String description = ""; //description of event
    private int color;
    private boolean isEvent;


    public EventEvent(String name, String location, Calendar startTime, Calendar endTime,
                      int color, boolean isEvent){
        if (name.equals(""))
        {
            Log.i(TAG, "Invalid Event Name");
        }
        if (startTime == null || endTime == null)
        {
            Log.i(TAG, "Invalid time");
        }


        this.setmName(name);
        this.setmStartTime(startTime);
        this.setmEndTime(endTime);
        this.setmLocation(location);
        this.setmColor(color);
        this.setEvent(isEvent);
    }


    public long getmId() {
        return Id;
    }

    public void setmId(long id)
    {
        this.Id = id;
    }

    public boolean isEvent() {
        return isEvent;
    }

    public void setEvent(boolean isEvent) {
        this.isEvent = isEvent;
    }

    public String getmName() {
        return name;
    }

    public void setmName(String name) {
        this.name = name;
    }

    public Calendar getmStartTime() {
        return startTime;
    }

    public void setmStartTime(Calendar startTime) {
        Calendar temp = Calendar.getInstance();
        if(startTime != null)
            temp.setTime(startTime.getTime());
        this.startTime = temp;
    }

    public Calendar getmEndTime() {
        return endTime;
    }

    public void setmEndTime(Calendar endTime) {
        Calendar temp = Calendar.getInstance();
        if(endTime != null)
            temp.setTime(endTime.getTime());
        this.endTime = temp;
    }

    public int getmColor() {
        return color;
    }

    public void setmColor(int color) {
        this.color = color;
    }

    public String getmLocation() {
        return location;
    }

    public void setmLocation(String location) {
        this.location = location;
    }


}
