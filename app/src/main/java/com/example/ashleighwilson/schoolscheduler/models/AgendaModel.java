package com.example.ashleighwilson.schoolscheduler.models;

import java.util.Calendar;

public class AgendaModel
{
    private String mClassName;
    private String mAgendaTitle;
    int mColor;
    Calendar mDueDate;

    public AgendaModel(String name, String title, int color, Calendar date)
    {
        this.mClassName = name;
        this.mAgendaTitle = title;
        this.mColor = color;
        this.mDueDate = date;
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

    public Calendar getDueDate()
    {
        return mDueDate;
    }

    public void setDueDate(Calendar date)
    {
        this.mDueDate = date;
    }
}
