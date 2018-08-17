package com.example.ashleighwilson.schoolscheduler.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SubjectsModel
{

    private String mTitle;
    private String mTeacher;

    /*public SubjectsModel(String title, String teacher)
    {
        this.mTitle = title;
        this.mTeacher = teacher;
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;} */

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

}
