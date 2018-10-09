package com.example.ashleighwilson.schoolscheduler.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AgendaModel
{
    private static final String TAG = AgendaModel.class.getSimpleName();

    private int mId;
    private String mClassName;
    private String mAgendaTitle;
    private int mColor;
    private String mDueDate;
    private boolean mBefore;
    private long mInterval;

    public AgendaModel(int id, String name, String title, String date, int color)
    {
        this.mId = id;
        this.mClassName = name;
        this.mAgendaTitle = title;
        this.mColor = color;
        this.mDueDate = date;
    }

    public AgendaModel() {

    }

    public String getClassName()
    {
        return mClassName;
    }

    public void setClassName(String name)
    {
        this.mClassName = name;
    }

    public String getAgendaTitle()
    {
        return mAgendaTitle;
    }

    public void setAgendaTitle(String title)
    {
        this.mAgendaTitle = title;
    }

    public int getmColor()
    {
        return mColor;
    }

    public void setmColor(int color)
    {
        this.mColor = color;
    }

    public String getDueDate()
    {
        return mDueDate;
    }

    public void setDueDate(String date)
    {
        this.mDueDate = date;
        setmInterval();
    }

    public boolean ismBefore() {
        return mBefore;
    }

    public long getmInterval() {
        return mInterval;
    }

    public void setmInterval()
    {
        String strThatDay = mDueDate;
        Log.i(TAG, "date: " + mDueDate);

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
        Date d = null;
        try{
            d = formatter.parse(strThatDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(d);
        Calendar today = Calendar.getInstance();
        long diff;
        if (thatDay.before(today))
        {
            mBefore = true;
            diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
        } else {
            mBefore = false;
            diff = thatDay.getTimeInMillis() - today.getTimeInMillis();
        }
        long days = diff / (24 * 60 * 60 * 1000);
        mInterval = days;
    }
}
