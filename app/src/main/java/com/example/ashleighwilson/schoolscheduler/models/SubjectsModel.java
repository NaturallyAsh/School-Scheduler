package com.example.ashleighwilson.schoolscheduler.models;

public class SubjectsModel
{

    public static String DEFAULT_COLOR = "#e0e0e0";
    private String mTitle;
    private String mTeacher;
    private int id;
    private String ID;
    private String mRoom;
    private String mColor;

    public SubjectsModel(String title, String teacher, String room, String color)
    {
        this.id = id;
        this.mTitle = title;
        this.mTeacher = teacher;
        this.mRoom = room;
        this.mColor = color;
    }
    public SubjectsModel(String title, String teacher, String room)
    {
        this.mTitle = title;
        this.mTeacher = teacher;
        this.mRoom = room;
    }

    public SubjectsModel(int id, String title, String teacher, String room)
    {
        this.id = id;
        this.mTitle = title;
        this.mTeacher = teacher;
        this.mRoom = room;
    }

    public SubjectsModel() {

    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getID()
    {
        return ID;
    }
    public void setID(String ID)
    {
        this.ID = ID;
    }

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

    public String getmColor()
    {
        return mColor;
    }

    public void setmColor(String color)
    {
        this.mColor = color;
    }

    public boolean match(SubjectsModel otherSubject)
    {
        return this.id == otherSubject.id && this.mTeacher.equals(otherSubject.mTeacher) &&
                this.mTitle.equals(otherSubject.mTitle)
                && this.mRoom.equals(otherSubject.mRoom)
                && this.mColor.equals(otherSubject.mColor);
    }

}
