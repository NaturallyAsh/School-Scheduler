package com.example.ashleighwilson.schoolscheduler.models;

import java.util.Calendar;

public class AgendaModel
{
    private int mId;
    private String mClassName;
    private String mAgendaTitle;
    private int mColor;
    private String mDueDate;

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
    }
}
