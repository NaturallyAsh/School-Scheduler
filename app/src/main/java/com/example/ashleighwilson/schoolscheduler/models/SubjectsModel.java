package com.example.ashleighwilson.schoolscheduler.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SubjectsModel implements Parcelable
{
    private String mTitle;
    private String mTeacher;
    private int id;
    private String mRoom;
    private int mColor;
    private String mStartTime;
    private String mEndTime;
    private String mRecurrence_rule;
    private String mRecurrence_option;
    private Calendar mConvertStartTime;
    private String mDayOfWeek;

    public SubjectsModel(int id, String title, String teacher, String room, int color, String start, String end,
                         String rule, String option, String dayOfWeek)
    {
        this.id = id;
        this.mTitle = title;
        this.mTeacher = teacher;
        this.mRoom = room;
        this.mColor = color;
        this.mStartTime = start;
        this.mEndTime = end;
        this.mRecurrence_rule = rule;
        this.mRecurrence_option = option;
        this.mDayOfWeek = dayOfWeek;
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
        mRecurrence_rule = in.readString();
        mRecurrence_option = in.readString();
        mDayOfWeek = in.readString();
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

    public Calendar getConvertStartTime() {
        return mConvertStartTime;
    }

    public void setConvertStartTime() {
        String convertString = mStartTime;

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
        Date d = null;
        try{
            d = formatter.parse(convertString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar thatTime = Calendar.getInstance();
        thatTime.setTime(d);

        mConvertStartTime = thatTime;
    }

    public void setmStartTime(String startTime)
    {
        this.mStartTime = startTime;
        if (startTime != null) {
            setConvertStartTime();
        }
    }

    public String getmEndTime()
    {
        return mEndTime;
    }

    public void setmEndTime(String endTime)
    {
        this.mEndTime = endTime;
    }

    public String getmRecurrence_rule() {
        return mRecurrence_rule;
    }
    public void setmRecurrence_rule(String rule) {
        this.mRecurrence_rule = rule;
    }
    public String getmRecurrence_option() {
        return mRecurrence_option;
    }
    public void setmRecurrence_option(String option) {
        this.mRecurrence_option = option;
    }

    public String getmDayOfWeek() {
        return mDayOfWeek;
    }
    public void setmDayOfWeek(String dayOfWeek) {
        this.mDayOfWeek = dayOfWeek;
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
        dest.writeString(mRecurrence_rule);
        dest.writeString(mRecurrence_option);
        dest.writeString(mDayOfWeek);
    }

}
