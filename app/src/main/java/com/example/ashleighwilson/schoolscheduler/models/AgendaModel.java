package com.example.ashleighwilson.schoolscheduler.models;

import java.util.Calendar;

public class AgendaModel
{
    private int mId;
    private String mClassName;
    private String mAgendaTitle;
    private int mColor;
    Calendar mDueDate;

    public AgendaModel(int id, String name, String title, Calendar date, int color)
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

    public Calendar getDueDate()
    {
        return mDueDate;
    }

    public void setDueDate(Calendar date)
    {
        this.mDueDate = date;
    }
}
