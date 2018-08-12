package com.example.ashleighwilson.schoolscheduler.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SubjectsModel implements Parcelable
{
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public SubjectsModel createFromParcel(Parcel in)
        {
            return new SubjectsModel(in);
        }
        public SubjectsModel[] newArray(int size)
        {
            return new SubjectsModel[size];
        }
    };

    private long id;
    private String mTitle;
    private String mTeacher;

    public SubjectsModel(String title, String teacher)
    {
        this.mTitle = title;
        this.mTeacher = teacher;
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

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

    public SubjectsModel(Parcel in)
    {
        this.mTitle = in.readString();
        this.mTeacher = in.readString();
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.mTitle);
        dest.writeString(this.mTeacher);
    }

    @Override
    public String toString()
    {
        return "Subject{" +
                "title='" + mTitle + '\'' +
                ", teacher='" + mTeacher + '\'' +
                '}';
    }
}
