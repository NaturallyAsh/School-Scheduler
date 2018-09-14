package com.example.ashleighwilson.schoolscheduler.timetable;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ashleighwilson.schoolscheduler.models.RecordingModel;
import com.example.ashleighwilson.schoolscheduler.timetable.WeekViewEvent.*;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@org.parceler.Parcel
public class Event
{
    int mId;
    String mName;
    String mDayOfMonth;
    String mStartTime;
    String mEndTime;
    int mColor;

    public Event(Parcel in)
    {
        mName = in.readString();
        mDayOfMonth = in.readString();
        mStartTime = in.readString();
        mEndTime = in.readString();
        mColor = in.readInt();
    }

    public Event(String name, String startTime, String endTime, int color)
    {
        //mId = id;
        mName = name;
        //mDayOfMonth = day;
        mStartTime = startTime;
        mEndTime = endTime;
        mColor = color;
    }

    public Event() {

    }

    public int getmId()
    {
        return mId;
    }

    public void setmId(int id)
    {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDayOfMonth() {
        return mDayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.mDayOfMonth = dayOfMonth;
    }

    public String getStartTime() {
        return mStartTime;
    }

    public void setStartTime(String startTime) {
        this.mStartTime = startTime;
    }

    public String getEndTime() {
        return mEndTime;
    }

    public void setEndTime(String endTime) {
        this.mEndTime = endTime;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    @SuppressLint("SimpleDateFormat")
    public WeekViewEvent toWeekViewEvent(){

        // Parse time.
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date start = new Date();
        Date end = new Date();
        try {
            start = sdf.parse(getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            end = sdf.parse(getEndTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Initialize start and end time.
        Calendar now = Calendar.getInstance();
        Calendar startTime = (Calendar) now.clone();
        startTime.setTimeInMillis(start.getTime());
        startTime.set(Calendar.YEAR, now.get(Calendar.YEAR));
        startTime.set(Calendar.MONTH, now.get(Calendar.MONTH));
        startTime.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.setTimeInMillis(end.getTime());
        endTime.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
        endTime.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
        endTime.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH));

        // Create an week view event.
        WeekViewEvent weekViewEvent = new WeekViewEvent();
        weekViewEvent.setName(getName());
        weekViewEvent.setStartTime(startTime);
        weekViewEvent.setEndTime(endTime);
        weekViewEvent.setColor((getColor()));

        return weekViewEvent;
    }

    /*@Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(mName);
        //dest.writeInt(mDayOfMonth);
        dest.writeString(mStartTime);
        dest.writeString(mEndTime);
        dest.writeInt(mColor);
    }

    public static final Parcelable.Creator<Event> CREATOR = new
            Parcelable.Creator<Event>()
            {
                @Override
                public Event createFromParcel(Parcel in) {
                    return new Event(in);
                }

                @Override
                public Event[] newArray(int size) {
                    return new Event[size];
                }
            }; */
}
