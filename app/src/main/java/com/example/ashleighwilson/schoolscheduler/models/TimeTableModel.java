package com.example.ashleighwilson.schoolscheduler.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeTableModel implements Parcelable
{
    private String tName;
    private String tType;
    private String tDay;
    private int tId;
    private String tStartHour;
    private String tEndHour;

    public TimeTableModel(Parcel in)
    {
        tName = in.readString();
        tType = in.readString();
        tDay = in.readString();
        tId = in.readInt();
        tStartHour = in.readString();
        tEndHour = in.readString();
    }

    public TimeTableModel(String name, String type, String day, String startHour, String endHour)
    {
        //tId = id;
        tName = name;
        tType = type;
        tDay = day;
        tStartHour = startHour;
        tEndHour = endHour;
    }

    public String gettName()
    {
        return tName;
    }

    public void settName(String name)
    {
        tName = name;
    }

    public String gettType()
    {
        return tType;
    }

    public void settType(String type)
    {
        tType = type;
    }

    public String gettDay()
    {
        return tDay;
    }

    public void settDay(String day)
    {
        tDay = day;
    }

    public int gettId()
    {
        return tId;
    }

    public void settId(int id)
    {
        tId = id;
    }

    public String gettStartHour()
    {
        return tStartHour;
    }

    public void settStartHour(String startHour)
    {
        tStartHour = startHour;
    }

    public String gettEndHour()
    {
        return tEndHour;
    }

    public void settEndHour(String endHour)
    {
        tEndHour = endHour;
    }


    public static final Parcelable.Creator<TimeTableModel> CREATOR = new
            Parcelable.Creator<TimeTableModel>()
            {
                @Override
                public TimeTableModel createFromParcel(Parcel in) {
                    return new TimeTableModel(in);
                }

                @Override
                public TimeTableModel[] newArray(int size) {
                    return new TimeTableModel[size];
                }
            };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(tName);
        dest.writeString(tType);
        dest.writeString(tDay);
        dest.writeInt(tId);
        dest.writeString(tStartHour);
        dest.writeString(tEndHour);
    }
}
