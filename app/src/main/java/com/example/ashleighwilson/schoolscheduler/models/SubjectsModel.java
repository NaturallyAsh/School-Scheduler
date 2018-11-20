package com.example.ashleighwilson.schoolscheduler.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectsModel implements Parcelable
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

    public SubjectsModel(Parcel in) {
        id = in.readInt();
        mTitle = in.readString();
        mTeacher = in.readString();
        mRoom = in.readString();
        mColor = in.readInt();
        mStartTime = in.readString();
        mEndTime = in.readString();
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

    public static final Parcelable.Creator<SubjectsModel> CREATOR = new
            Parcelable.Creator<SubjectsModel>() {
                @Override
                public SubjectsModel createFromParcel(Parcel in) {
                    return new SubjectsModel(in);
                }
                @Override
                public SubjectsModel[] newArray(int size) {
                    return new SubjectsModel[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(id);
        dest.writeString(mTitle);
        dest.writeString(mTeacher);
        dest.writeString(mRoom);
        dest.writeInt(mColor);
        dest.writeString(mStartTime);
        dest.writeString(mEndTime);
    }

}
