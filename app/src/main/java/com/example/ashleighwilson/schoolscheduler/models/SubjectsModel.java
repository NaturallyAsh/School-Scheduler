package com.example.ashleighwilson.schoolscheduler.models;

public class SubjectsModel
{
    private String mTitle;
    private String mTeacher;
    private int id;
    private String mRoom;
    private int mColor;
    private String mStartTime;
    private String mEndTime;

    public SubjectsModel(int id, String title, String teacher, String room, int color, String start, String end)
    {
        this.id = id;
        this.mTitle = title;
        this.mTeacher = teacher;
        this.mRoom = room;
        this.mColor = color;
        this.mStartTime = start;
        this.mEndTime = end;
    }

    public SubjectsModel() {

    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getmTitle()
    {
        return mTitle;
    }

    public void setmTitle(String title)
    {
        this.mTitle = title;
    }

    public String getmTeacher()
    {
        return mTeacher;
    }

    public void setmTeacher(String teacher)
    {
        this.mTeacher = teacher;
    }

    public String getmRoom()
    {
        return mRoom;
    }

    public void setmRoom(String room)
    {
        this.mRoom = room;
    }

    public int getmColor()
    {
        return mColor;
    }

    public void setmColor(int color)
    {
        this.mColor = color;
    }

    public String getmStartTime()
    {
        return mStartTime;
    }

    public void setmStartTime(String startTime)
    {
        this.mStartTime = startTime;
    }

    public String getmEndTime()
    {
        return mEndTime;
    }

    public void setmEndTime(String endTime)
    {
        this.mEndTime = endTime;
    }

}
